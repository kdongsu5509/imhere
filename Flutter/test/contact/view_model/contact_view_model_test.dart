import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:iamhere/contact/repository/contact_entity.dart';
import 'package:iamhere/contact/repository/contact_repository.dart';
import 'package:iamhere/contact/repository/contact_repository_provider.dart';
import 'package:iamhere/contact/view_model/contact.dart';
import 'package:iamhere/contact/view_model/contact_view_model.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';

import 'contact_view_model_test.mocks.dart';

const DATA_CHANNEL_NAME = 'com.iamhere.app/contacts';
const PERMISSION_CHANNEL_NAME = 'flutter.baseflow.com/permissions/methods';
const SELECT_CONTACT_METHOD = 'selectContact';

@GenerateMocks([ContactRepository])
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  late MockContactRepository mockRepository;
  late ProviderContainer container;

  final entityAlice = ContactEntity(id: 1, name: 'Alice', number: '111');
  final entityBob = ContactEntity(id: 2, name: 'Bob', number: '222');

  setUp(() async {
    mockRepository = MockContactRepository();

    container = ProviderContainer(
      overrides: [contactRepositoryProvider.overrideWithValue(mockRepository)],
    );

    when(mockRepository.findAll()).thenAnswer((_) async => [entityAlice]);

    _initializeMockHandlers();

    await container.read(contactViewModelProvider.future);
  });

  tearDown(() {
    container.dispose();
    _initializeMockHandlers();
  });

  group('ContactViewModel', () {
    /**
     * selectContact
     */
    test('1. selectContact 성공 시 Contact 객체를 반환하고 상태를 업데이트해야 한다', () async {
      final vm = container.read(contactViewModelProvider.notifier);
      final mockContact = {'name': '고동수', 'number': '01012345678'};
      final savedEntity = ContactEntity(
        id: 3,
        name: '고동수',
        number: '01012345678',
      );

      _setMockPermissionHandler(PermissionStatus.limited.index);
      _setMockDataChannelHandler((MethodCall methodCall) async {
        if (methodCall.method == SELECT_CONTACT_METHOD) {
          return mockContact;
        }
        return null;
      });

      when(mockRepository.save(any)).thenAnswer((_) async => savedEntity);

      final Contact? result = await vm.selectContact();

      expect(result, isNotNull);
      expect(result!.name, '고동수');
      expect(container.read(contactViewModelProvider).value!.length, 2);
    });

    test(
      '2. 권한 허용 후 네이티브 호출 실패 시 PlatformException을 잡아 Exception을 던져야 한다',
      () async {
        final vm = container.read(contactViewModelProvider.notifier);
        _setMockPermissionHandler(PermissionStatus.granted.index);

        _setMockDataChannelHandler((MethodCall methodCall) async {
          if (methodCall.method == SELECT_CONTACT_METHOD) {
            throw PlatformException(code: 'ERROR', message: '네이티브 오류');
          }
          return null;
        });

        expect(
          vm.selectContact(),
          throwsA(
            predicate(
              (e) => e is Exception && e.toString().contains('연락처 선택에 실패하였습니다'),
            ),
          ),
        );

        await Future.delayed(Duration.zero);
        expect(container.read(contactViewModelProvider).value!.length, 1);
      },
    );

    test('3. 권한이 isDenied면 Exception("연락처 권한을 허용해주세요!")를 던져야 한다', () async {
      final vm = container.read(contactViewModelProvider.notifier);
      _setMockPermissionHandler(PermissionStatus.denied.index);

      expect(vm.selectContact(), throwsA(isA<Exception>()));

      expect(
        vm.selectContact(),
        throwsA(
          predicate(
            (e) => e is Exception && e.toString().contains('연락처 권한을 허용해주세요!'),
          ),
        ),
      );
    });

    test('4. 권한이 isRestricted이면 Exception를 던져야 한다', () async {
      final vm = container.read(contactViewModelProvider.notifier);
      _setMockPermissionHandler(PermissionStatus.restricted.index);

      expect(vm.selectContact(), throwsA(isA<Exception>()));

      expect(
        vm.selectContact(),
        throwsA(
          predicate(
            (e) =>
                e is Exception &&
                e.toString().contains(
                  '사용자 기기의 정책으로 인해 접근이 불가능 합니다. 설정에서 정책을 변경해주세요',
                ),
          ),
        ),
      );
    });

    test('5. 권한이 isPermanentlyDenied이면 Exception를 던져야 한다', () async {
      final vm = container.read(contactViewModelProvider.notifier);
      _setMockPermissionHandler(PermissionStatus.permanentlyDenied.index);

      expect(vm.selectContact(), throwsA(isA<Exception>()));

      expect(
        vm.selectContact(),
        throwsA(
          predicate(
            (e) =>
                e is Exception &&
                e.toString().contains(
                  '연락처 권한이 영구적으로 거부되었습니다. 설정에서 수동으로 허용해주세요.',
                ),
          ),
        ),
      );
    });
  });

  group('ContactViewModel.deleteContact', () {
    setUp(() async {
      when(
        mockRepository.findAll(),
      ).thenAnswer((_) async => [entityAlice, entityBob]);
      container = ProviderContainer(
        overrides: [
          contactRepositoryProvider.overrideWithValue(mockRepository),
        ],
      );
      await container.read(contactViewModelProvider.future);
      // 이 시점에서 상태는 Alice와 Bob, 2개입니다.
    });

    test(
      '6. Repository delete 실패 시 상태가 이전 상태로 롤백되어야 한다 (낙관적 업데이트 검증)',
      () async {
        final vm = container.read(contactViewModelProvider.notifier);
        final initialList = container.read(contactViewModelProvider).value!;

        when(mockRepository.delete(1)).thenThrow(Exception('DB 삭제 실패'));

        await vm.deleteContact(1);

        final finalState = container.read(contactViewModelProvider).value;

        expect(finalState!.length, 2);
        expect(finalState, equals(initialList));
        expect(finalState.any((c) => c.id == 1), isTrue);
        verify(mockRepository.delete(1)).called(1);
      },
    );

    test('7. delete 성공 시 상태에서 해당 항목이 제거되어야 한다', () async {
      final vm = container.read(contactViewModelProvider.notifier);
      when(mockRepository.delete(2)).thenAnswer((_) async {});

      await vm.deleteContact(2);

      final finalState = container.read(contactViewModelProvider).value;

      expect(finalState!.length, 1);
      expect(finalState.any((c) => c.id == 2), isFalse);
      expect(finalState.any((c) => c.id == 1), isTrue);
      verify(mockRepository.delete(2)).called(1);
    });
  });
}

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
