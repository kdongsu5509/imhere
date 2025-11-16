import 'dart:async';
import 'dart:convert';
import 'dart:developer';
import 'package:geolocator/geolocator.dart';
import 'package:iamhere/contact/repository/contact_entity.dart';
import 'package:iamhere/contact/repository/contact_repository_provider.dart';
import 'package:iamhere/geofence/repository/geofence_entity.dart';
import 'package:iamhere/geofence/repository/geofence_repository_provider.dart';
import 'package:iamhere/geofence/service/my_location_service.dart';
import 'package:iamhere/geofence/service/sms_permission_service.dart';
import 'package:iamhere/geofence/service/sms_service.dart';
import 'package:iamhere/record/repository/geofence_record_entity.dart';
import 'package:iamhere/record/repository/geofence_record_repository_provider.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'geofence_monitoring_service.g.dart';

/// 지오펜스 모니터링 서비스
/// 활성화된 지오펜스를 모니터링하고 진입 시 SMS를 전송합니다.
@Riverpod(keepAlive: true)
class GeofenceMonitoringService extends _$GeofenceMonitoringService {
  StreamSubscription<Position>? _positionStreamSubscription;
  final Set<int> _enteredGeofenceIds = {}; // 이미 진입한 지오펜스 ID (중복 전송 방지)
  Timer? _resetTimer; // 일정 시간 후 진입 상태 초기화 (같은 위치에 다시 도착했을 때 다시 전송 가능)

  @override
  Future<void> build() async {
    // 초기화
  }

  /// 지오펜스 모니터링 시작
  Future<void> startMonitoring() async {
    // 이미 모니터링 중이면 중지
    await stopMonitoring();

    log('지오펜스 모니터링 시작');

    // 위치 권한 확인 및 요청
    try {
      final locationService = MyLocationService();
      final permissionState = await locationService
          .requestLocationPermissions();

      if (permissionState != LocationPermissionState.grantedAlways &&
          permissionState != LocationPermissionState.grantedWhenInUse) {
        log('위치 권한이 허용되지 않아 모니터링을 시작할 수 없습니다: ${permissionState.name}');
        throw Exception('위치 권한이 필요합니다. 설정에서 위치 권한을 허용해주세요.');
      }

      // SMS 권한 확인 (Android만)
      try {
        final smsPermissionService = SmsPermissionService();
        final hasSmsPermission = await smsPermissionService
            .isSmsPermissionGranted();
        if (!hasSmsPermission) {
          log('SMS 권한이 없습니다. 지오펜스 진입 시 SMS 앱을 열어 전송하도록 합니다.');
        } else {
          log('SMS 권한이 허용되어 자동 전송이 가능합니다.');
        }
      } catch (e) {
        log('SMS 권한 확인 중 오류: $e');
      }

      // 위치 업데이트 스트림 설정
      final LocationSettings locationSettings = LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 10, // 10미터마다 위치 업데이트
      );

      _positionStreamSubscription =
          Geolocator.getPositionStream(
            locationSettings: locationSettings,
          ).listen(
            (Position position) {
              _checkGeofences(position);
            },
            onError: (error) {
              log('위치 스트림 오류: $error');
            },
          );

      log('지오펜스 모니터링 시작 완료');
    } catch (e) {
      log('지오펜스 모니터링 시작 실패: $e');
      rethrow;
    }
  }

  /// 지오펜스 모니터링 중지
  Future<void> stopMonitoring() async {
    log('지오펜스 모니터링 중지');
    await _positionStreamSubscription?.cancel();
    _positionStreamSubscription = null;
    _enteredGeofenceIds.clear();
    _resetTimer?.cancel();
    _resetTimer = null;
  }

  /// 현재 위치에서 활성화된 지오펜스 확인
  Future<void> _checkGeofences(Position currentPosition) async {
    try {
      final repository = ref.read(geofenceRepositoryProvider);
      final allGeofences = await repository.findAll();

      // 활성화된 지오펜스만 필터링
      final activeGeofences = allGeofences.where((g) => g.isActive).toList();

      for (final geofence in activeGeofences) {
        // 이미 진입한 지오펜스는 건너뛰기
        if (_enteredGeofenceIds.contains(geofence.id)) {
          continue;
        }

        // 거리 계산
        final distance = Geolocator.distanceBetween(
          currentPosition.latitude,
          currentPosition.longitude,
          geofence.lat,
          geofence.lng,
        );

        // 지오펜스 반경 내에 있는지 확인
        if (distance <= geofence.radius) {
          log(
            '지오펜스 진입 감지: ${geofence.name}, 거리: ${distance.toStringAsFixed(2)}m',
          );

          // 진입한 지오펜스 ID 저장
          _enteredGeofenceIds.add(geofence.id!);

          // SMS 전송
          await _sendSmsForGeofence(geofence);

          // 1시간 후 진입 상태 초기화 (같은 위치에 다시 도착했을 때 다시 전송 가능)
          _resetTimer?.cancel();
          _resetTimer = Timer(const Duration(hours: 1), () {
            _enteredGeofenceIds.clear();
            log('지오펜스 진입 상태 초기화');
          });
        }
      }
    } catch (e) {
      log('지오펜스 확인 오류: $e');
    }
  }

  /// 지오펜스 진입 시 SMS 전송
  Future<void> _sendSmsForGeofence(GeofenceEntity geofence) async {
    try {
      // 연락처 ID 리스트 파싱
      final List<dynamic> contactIdsJson = jsonDecode(geofence.contactIds);
      final List<int> contactIds = contactIdsJson
          .map((id) => id as int)
          .toList();

      if (contactIds.isEmpty) {
        log('연락처가 없어 SMS를 전송할 수 없습니다.');
        return;
      }

      // 연락처 정보 가져오기
      final contactRepository = ref.read(contactRepositoryProvider);
      final allContacts = await contactRepository.findAll();

      // 해당 ID의 연락처 필터링
      final recipients = allContacts
          .where((contact) => contactIds.contains(contact.id))
          .toList();

      if (recipients.isEmpty) {
        log('연락처를 찾을 수 없습니다.');
        return;
      }

      // 전화번호 리스트 추출 (숫자만 남기기)
      final phoneNumbers = recipients
          .map((contact) => contact.number.replaceAll(RegExp(r'[^\d]'), ''))
          .toList();

      // SMS 전송
      final smsService = SmsService();
      final success = await smsService.sendSmsToMultipleRecipients(
        phoneNumbers: phoneNumbers,
        message: geofence.message,
      );

      if (success) {
        log('SMS 전송 성공: ${geofence.name}');

        // 기록 저장
        await _saveRecord(geofence, recipients);
      } else {
        log('SMS 전송 실패: ${geofence.name}');
      }
    } catch (e) {
      log('SMS 전송 오류: $e');
    }
  }

  /// 지오펜스 진입 기록 저장
  Future<void> _saveRecord(
    GeofenceEntity geofence,
    List<ContactEntity> recipients,
  ) async {
    try {
      if (geofence.id == null) return;

      // 수신자 이름 리스트 생성
      final recipientNames = recipients.map((contact) => contact.name).toList();

      final recordRepository = ref.read(geofenceRecordRepositoryProvider);

      final record = GeofenceRecordEntity(
        geofenceId: geofence.id!,
        geofenceName: geofence.name,
        message: geofence.message,
        recipients: jsonEncode(recipientNames),
        createdAt: DateTime.now(),
      );

      await recordRepository.save(record);
      log('지오펜스 기록 저장 완료: ${geofence.name}');
    } catch (e) {
      log('지오펜스 기록 저장 오류: $e');
    }
  }
}
