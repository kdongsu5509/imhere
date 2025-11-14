import 'package:flutter/services.dart';
import 'package:iamhere/contact/view_model/contact.dart';
import 'package:iamhere/contact/view_model/contact_view_model_interface.dart';
import 'package:permission_handler/permission_handler.dart';

class ContactViewModel implements ContactViewModelInterface {
  static final channelName = 'com.iamhere.app/contacts';

  static final methodChannel = MethodChannel(channelName);

  @override
  Future<Contact?> selectContact() async {
    try {
      // 1. 네이티브 메서드 호출
      final Map<dynamic, dynamic>? result = await methodChannel.invokeMethod(
        'selectContact', // 네이티브에서 구현할 메서드 이름
      );

      if (result != null) {
        // 2. 결과 파싱 및 변환 (선택된 하나의 연락처만 Map 형태로 옴)
        final contact = Contact.fromJson(
          Map<String, dynamic>.from(result),
        );
        print("선택된 연락처: 이름 : ${contact.name} || 연락처 : ${contact.number}");
        return contact;
      }
    } on PlatformException catch (e) {
      print("네이티브 연락처 선택 실패: ${e.message}");
    }
    return null;
  }

  @override
  Future<List<Contact>> importContact() async {
    List<Contact> contacts = [];

    // 2. 연락처 권한 요청
    var status = await Permission.contacts.request();

    if (status.isGranted) {
      try {
        // 3. 네이티브 메서드 호출
        final List<dynamic>? result = await methodChannel.invokeMethod(
          'importContact',
        );

        if (result != null) {
          // 4. 결과 파싱 및 변환
          contacts = result
              // 문제의 캐스팅을 안전한 Map<String, dynamic>.from()으로 대체
              .map(
                (data) =>
                    Contact.fromJson(Map<String, dynamic>.from(data as Map)),
              )
              .toList();

          contacts.forEach(
            (contact) =>
                print("이름 : ${contact.name} || 연락처 : ${contact.number}"),
          );
          print('연락처 ${contacts.length}개 로드 성공');
        }
      } on PlatformException catch (e) {
        print("네이티브 연락처 로드 실패: ${e.message}");
        // 에러 처리 로직
      }
    } else if (status.isDenied) {
      print("연락처 권한이 거부되었습니다.");
    } else if (status.isPermanentlyDenied) {
      print("설정에서 연락처 권한을 활성화해주세요.");
      openAppSettings(); // 사용자에게 설정 열기 유도
    }

    return contacts;
  }
}
