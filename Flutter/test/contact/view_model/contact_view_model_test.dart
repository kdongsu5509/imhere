// test/contact/view_model/contact_view_model_test.dart

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
// 테스트 대상 및 모델 임포트 (업로드된 파일 경로 기반)
import 'package:iamhere/contact/view_model/contact.dart';
import 'package:iamhere/contact/view_model/contact_view_model.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  // 1. 테스트 환경 설정 (MethodChannel Mocking을 위해 필수)
  TestWidgetsFlutterBinding.ensureInitialized();

  // MethodChannel 이름 정의
  const channelName = 'com.iamhere.app/contacts';
  const permissionChannelName = 'flutter.baseflow.com/permissions/methods';

  late ContactViewModel viewModel;

  setUp(() {
    viewModel = ContactViewModel();

    // 테스트 시작 시, 사용된 모든 MethodChannel Mock 핸들러를 초기화합니다.
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(const MethodChannel(channelName), null);
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
          const MethodChannel(permissionChannelName),
          null,
        );
  });

  group('ContactViewModel - importContact Test', () {
    // 🚀 시나리오 1: 권한 허용 및 연락처 로드 성공
    test('권한 허용 후 연락처 로드 성공 시 Contact 목록을 반환해야 한다', () async {
      // ARRANGE 1: 권한 허용 Mocking
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(
            const MethodChannel(permissionChannelName),
            (MethodCall methodCall) async {
              if (methodCall.method == 'requestPermissions') {
                return {
                  Permission.contacts.value: PermissionStatus.granted.index,
                };
              }
              return null;
            },
          );

      // ARRANGE 2: MethodChannel 성공 응답 Mocking
      final mockContacts = [
        {'name': '고동수', 'number': '01012345678'},
        {'name': '테스트', 'number': '01098765432'},
      ];

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(const MethodChannel(channelName), (
            MethodCall methodCall,
          ) async {
            if (methodCall.method == 'importContact') {
              return mockContacts;
            }
            return null;
          });

      // ACT: 함수 호출
      final List<Contact> result = await viewModel.importContact();

      // ASSERT: 결과 검증 (Contact 객체의 필드를 직접 검증)
      expect(result, isNotNull);
      expect(result.length, 2);
      expect(result.first.name, '고동수');
      expect(result.last.number, '01098765432');
    });

    // 🚀 시나리오 2: 네이티브 호출 실패 (PlatformException 발생)
    test('권한 허용 후 네이티브 호출 실패 시 빈 리스트를 반환해야 한다', () async {
      // ARRANGE 1: 권한 허용 Mocking
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(
            const MethodChannel(permissionChannelName),
            (MethodCall methodCall) async {
              if (methodCall.method == 'requestPermissions') {
                return {
                  Permission.contacts.value: PermissionStatus.granted.index,
                };
              }
              return null;
            },
          );

      // ARRANGE 2: MethodChannel 실패 Mocking
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(const MethodChannel(channelName), (
            MethodCall methodCall,
          ) async {
            if (methodCall.method == 'importContact') {
              // PlatformException throw를 Mocking하여 네이티브 오류 상황을 재현
              throw PlatformException(code: 'ERROR', message: '네이티브 오류 발생');
            }
            return null;
          });

      // ACT: 함수 호출
      final result = await viewModel.importContact();

      // ASSERT: 빈 리스트 반환 검증
      expect(result, isEmpty);
    });

    // 🚀 시나리오 3: 권한 거부 시 빈 리스트 반환
    test('권한이 거부되면 네이티브 호출 없이 빈 리스트를 반환해야 한다', () async {
      // ARRANGE: 권한 거부 Mocking
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(
            const MethodChannel(permissionChannelName),
            (MethodCall methodCall) async {
              if (methodCall.method == 'requestPermissions') {
                // Permission.contacts.request()가 호출되면 denied를 반환
                return {
                  Permission.contacts.value: PermissionStatus.denied.index,
                };
              }
              return null;
            },
          );

      // ACT: 함수 호출
      final result = await viewModel.importContact();

      // ASSERT: 빈 리스트 반환 검증
      expect(result, isEmpty);
    });

    // 🚀 시나리오 4: 영구 거부 시 빈 리스트를 반환하고 openAppSettings가 호출됨 (선택적)
    // NOTE: openAppSettings는 Mocking이 어렵기 때문에, 빈 리스트 반환만 검증합니다.
    test('권한이 영구 거부되면 빈 리스트를 반환해야 한다', () async {
      // ARRANGE: 권한 영구 거부 Mocking
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(
            const MethodChannel(permissionChannelName),
            (MethodCall methodCall) async {
              if (methodCall.method == 'requestPermissions') {
                // Permission.contacts.request()가 호출되면 permanentlyDenied를 반환
                return {
                  Permission.contacts.value:
                      PermissionStatus.permanentlyDenied.index,
                };
              }
              return null;
            },
          );

      // ACT: 함수 호출
      final result = await viewModel.importContact();

      // ASSERT: 빈 리스트 반환 검증
      expect(result, isEmpty);
    });
  });
}
