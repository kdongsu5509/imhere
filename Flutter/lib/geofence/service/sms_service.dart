import 'dart:io';
import 'package:flutter/services.dart';
import 'package:iamhere/geofence/service/sms_permission_service.dart';
import 'package:url_launcher/url_launcher.dart';

class SmsService {
  final SmsPermissionService _permissionService = SmsPermissionService();
  static const MethodChannel _channel = MethodChannel(
    'com.kdongsu5509.iamhere/sms',
  );

  /// SMS를 자동으로 전송합니다.
  ///
  /// Android: SEND_SMS 권한이 있으면 자동으로 전송
  /// iOS: SMS 앱을 열어서 사용자가 전송하도록 함
  ///
  /// [phoneNumbers] 전화번호 리스트 (예: ['01012345678', '01087654321'])
  /// [message] 전송할 메시지 내용
  ///
  /// 반환값: 성공 여부
  Future<bool> sendSms({
    required List<String> phoneNumbers,
    required String message,
  }) async {
    try {
      if (phoneNumbers.isEmpty) {
        return false;
      }

      // 전화번호에서 숫자만 추출
      final cleanPhoneNumbers = phoneNumbers
          .map((phone) => phone.replaceAll(RegExp(r'[^\d]'), ''))
          .where((phone) => phone.isNotEmpty)
          .toList();

      if (cleanPhoneNumbers.isEmpty) {
        return false;
      }

      if (Platform.isAndroid) {
        // Android: 자동 SMS 전송 시도
        return await _sendSmsAndroid(cleanPhoneNumbers, message);
      } else {
        // iOS: SMS 앱 열기 (자동 전송 불가)
        return await _sendSmsIOS(cleanPhoneNumbers, message);
      }
    } catch (e) {
      print('SMS 전송 실패: $e');
      return false;
    }
  }

  /// Android에서 SMS 자동 전송
  Future<bool> _sendSmsAndroid(
    List<String> phoneNumbers,
    String message,
  ) async {
    try {
      // SMS 권한 확인 및 요청
      final hasPermission = await _permissionService
          .requestAndCheckSmsPermission();

      if (!hasPermission) {
        print('SMS 권한이 없어 자동 전송할 수 없습니다. SMS 앱을 엽니다.');
        // 권한이 없으면 기존 방식으로 SMS 앱 열기
        return await _openSmsApp(phoneNumbers, message);
      }

      // 플랫폼 채널을 통해 네이티브 코드로 SMS 전송
      try {
        final result = await _channel.invokeMethod<bool>('sendSms', {
          'phoneNumbers': phoneNumbers,
          'message': message,
        });
        return result ?? false;
      } catch (e) {
        print('플랫폼 채널 SMS 전송 실패: $e');
        // 실패 시 SMS 앱 열기로 대체
        return await _openSmsApp(phoneNumbers, message);
      }
    } catch (e) {
      print('Android SMS 자동 전송 실패: $e');
      // 실패 시 SMS 앱 열기로 대체
      return await _openSmsApp(phoneNumbers, message);
    }
  }

  /// iOS에서 SMS 전송 시도
  /// iOS는 플랫폼 제한으로 직접 자동 전송이 불가능하므로 SMS 앱을 엽니다.
  /// 메시지가 미리 채워져 있어 사용자가 한 번의 탭으로 전송할 수 있습니다.
  Future<bool> _sendSmsIOS(List<String> phoneNumbers, String message) async {
    // iOS는 자동 전송 불가, SMS 앱 열기
    // 여러 수신자가 있는 경우 첫 번째 수신자에게만 전송
    return await _openSmsApp(phoneNumbers, message);
  }

  /// SMS 앱 열기 (권한이 없거나 자동 전송 실패 시 사용)
  Future<bool> _openSmsApp(List<String> phoneNumbers, String message) async {
    try {
      Uri uri;

      if (Platform.isIOS) {
        // iOS: 여러 수신자 지원 안 함, 첫 번째 수신자만 사용
        // 국제 형식으로 변환 (한국: +82)
        String phoneNumber = phoneNumbers.first;
        if (phoneNumber.startsWith('0')) {
          phoneNumber = '+82${phoneNumber.substring(1)}';
        } else if (!phoneNumber.startsWith('+')) {
          phoneNumber = '+82$phoneNumber';
        }
        uri = Uri.parse(
          'sms:$phoneNumber&body=${Uri.encodeComponent(message)}',
        );
      } else {
        // Android: 여러 수신자 지원 (쉼표로 구분)
        final recipients = phoneNumbers.join(',');
        uri = Uri(
          scheme: 'sms',
          path: recipients,
          queryParameters: {'body': message},
        );
      }

      if (await canLaunchUrl(uri)) {
        return await launchUrl(uri, mode: LaunchMode.externalApplication);
      } else {
        return false;
      }
    } catch (e) {
      print('SMS 앱 열기 실패: $e');
      return false;
    }
  }

  /// 여러 수신자에게 SMS를 전송합니다.
  ///
  /// Android: 권한이 있으면 자동 전송, 없으면 SMS 앱 열기
  /// iOS: SMS 앱 열기
  Future<bool> sendSmsToMultipleRecipients({
    required List<String> phoneNumbers,
    required String message,
  }) async {
    return await sendSms(phoneNumbers: phoneNumbers, message: message);
  }
}
