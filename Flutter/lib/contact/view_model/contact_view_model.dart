import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:iamhere/contact/repository/contact_repository_provider.dart';
import 'package:iamhere/contact/view_model/contact.dart';
import 'package:iamhere/contact/view_model/contact_adapter.dart';
import 'package:iamhere/contact/view_model/contact_view_model_interface.dart';
import 'package:permission_handler/permission_handler.dart';

import '../repository/contact_repository.dart';

part 'contact_view_model.g.dart';

@Riverpod(keepAlive: true)
class ContactViewModel extends _$ContactViewModel
    implements ContactViewModelInterface {
  late ContactRepository _repository;

  /// AsyncNotifierProvider 초기화
  @override
  Future<List<Contact>> build() async {
    _repository = ref.watch(contactRepositoryProvider);

    final foundEntities = await _repository.findAll();
    return foundEntities.map((entity) {
      return ContactAdapter.toClass(entity);
    }).toList();
  }

  static const channelName = 'com.iamhere.app/contacts';
  static const methodChannel = MethodChannel(channelName);

  ///methodChannel (Native Linking Code)
  @override
  Future<Contact?> selectContact() async {
    await _checkPermission();

    try {
      final Map<dynamic, dynamic>? result = await methodChannel.invokeMethod(
        'selectContact',
      );

      if (result != null) {
        final contact = Contact.fromJson(Map<String, dynamic>.from(result));

        final savedEntity = await _repository.save(
          ContactAdapter.toEntity(contact),
        );
        final savedContact = ContactAdapter.toClass(savedEntity);

        state = AsyncValue.data([...state.value!, savedContact]);

        return savedContact;
      }
    } on PlatformException catch (e) {
      log("네이티브에서 연락처 선택 실패 : [이유] -> $e");
      throw Exception("연락처 선택에 실패하였습니다");
    }
    return null;
  }

  @override
  Future<void> deleteContact(int id) async {
    if (!state.hasValue) {
      return;
    }

    final previousState = state;
    final originalList = state.value!;

    try {
      final updatedList = originalList.where((c) => c.id != id).toList();
      state = AsyncValue.data(updatedList);

      await _repository.delete(id);
    } catch (e, stack) {
      state = previousState;
      log('연락처 삭제 실패 및 롤백: $e');
    }
  }

  Future<void> _checkPermission() async {
    final status = await Permission.contacts.request();
    if (status.isGranted || status.isLimited) {
      return;
    }

    if (status.isDenied) {
      throw Exception("연락처 권한을 허용해주세요!");
    }

    if (status.isRestricted) {
      throw Exception("사용자 기기의 정책으로 인해 접근이 불가능 합니다. 설정에서 정책을 변경해주세요");
    }

    if (status.isPermanentlyDenied) {
      throw Exception("연락처 권한이 영구적으로 거부되었습니다. 설정에서 수동으로 허용해주세요.");
    }
  }
}
