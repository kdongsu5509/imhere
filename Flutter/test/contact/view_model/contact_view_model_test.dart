import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:iamhere/contact/view_model/contact.dart';
import 'package:iamhere/contact/view_model/contact_view_model.dart';
import 'package:permission_handler/permission_handler.dart';

const DATA_CHANNEL_NAME = 'com.iamhere.app/contacts';
const PERMISSION_CHANNEL_NAME = 'flutter.baseflow.com/permissions/methods';

const SELECT_CONTACT_METHOD = 'selectContact';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late ContactViewModel viewModel;

  setUp(() {
    viewModel = ContactViewModel();
    _initializeMockHandlers();
  });

  group('ContactViewModel - Contact Selection & Import Tests', () {
    test('네이티브에서 단일 연락처 불러오기 성공 시 Contact 객체를 반환해야 한다', () async {
      // given
      final mockContact = {'name': '고동수', 'number': '01012345678'};

      _setMockPermissionHandler(PermissionStatus.granted.index);
      _setMockDataChannelHandler((MethodCall methodCall) async {
        if (methodCall.method == SELECT_CONTACT_METHOD) {
          return mockContact;
        }
        return null;
      });

      // when
      final Contact? result = await viewModel.selectContact();

      // then
      expect(result, isNotNull);
      expect(result!.name, '고동수');
      expect(result.number, '01012345678');
    });

    //아래부터는 실패

    test('권한 허용 후 네이티브 호출 실패 시 빈 리스트를 반환해야 한다 (PlatformException)', () async {
      // given
      const NOT_EXIST_CONTACT_METHOD_NAME = 'notExist'; // importContact가 사용하는 메서드 이름 가정
      _setMockPermissionHandler(PermissionStatus.granted.index);

      _setMockDataChannelHandler((MethodCall methodCall) async {
        if (methodCall.method == NOT_EXIST_CONTACT_METHOD_NAME) {
          throw PlatformException(code: 'ERROR', message: '네이티브 오류');
        }
        return null;
      });

      // when
      final result = await viewModel.importContact();

      // then
      expect(result, isEmpty);
    });

    test('권한이 거부되면 네이티브 호출 없이 빈 리스트를 반환해야 한다', () async {
      // given
      _setMockPermissionHandler(PermissionStatus.denied.index);

      // when
      final result = await viewModel.importContact();

      // then
      expect(result, isEmpty);
    });

    test('권한이 영구 거부되면 빈 리스트를 반환해야 한다', () async {
      // given
      _setMockPermissionHandler(PermissionStatus.permanentlyDenied.index);

      // when
      final result = await viewModel.importContact();

      // then
      expect(result, isEmpty);
    });
  });
}

// ----------------------------------------------------------------------
// 헬퍼 함수
// ----------------------------------------------------------------------

void _initializeMockHandlers() {
  TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
      .setMockMethodCallHandler(const MethodChannel(DATA_CHANNEL_NAME), null);

  TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
      .setMockMethodCallHandler(
        const MethodChannel(PERMISSION_CHANNEL_NAME),
        null,
      );
}

void _setMockPermissionHandler(int permissionStatusIndex) {
  TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
      .setMockMethodCallHandler(const MethodChannel(PERMISSION_CHANNEL_NAME), (
        MethodCall methodCall,
      ) async {
        if (methodCall.method == 'requestPermissions') {
          return {Permission.contacts.value: permissionStatusIndex};
        }
        return null;
      });
}

void _setMockDataChannelHandler(
  Future<Object?>? Function(MethodCall message)? handler,
) {
  TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
      .setMockMethodCallHandler(
        const MethodChannel(DATA_CHANNEL_NAME),
        handler,
      );
}
