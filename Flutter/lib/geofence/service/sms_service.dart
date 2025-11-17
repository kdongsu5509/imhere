import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:iamhere/common/external_dio/dio_provider.dart';
import 'package:iamhere/geofence/service/sms_permission_service.dart';

class SmsService {
  final Ref ref;
  SmsService({required this.ref});
  final SmsPermissionService _permissionService = SmsPermissionService();
  static const MethodChannel _channel = MethodChannel(
    'com.kdongsu5509.iamhere/sms',
  );

  /// SMS를 자동으로 전송합니다.
  ///
  /// Android: SEND_SMS 권한이 있으면 자동으로 전송
  /// iOS: SMS 앱을 열어서 사용자가 전송하도록 함
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
        debugPrint('SMS 권한이 없어 자동 전송할 수 없습니다. SMS 앱을 엽니다.');
      }

      try {
        return await _sendSMSViaAndroidMethodChannel(phoneNumbers, message);
      } catch (e) {
        return await _handleFailCaseOfSendingSMS(
          e,
          phoneNumbers,
          message,
          "안드로이드에서 전송 과정에서 오류 발생",
        );
      }
    } catch (e) {
      return await _handleFailCaseOfSendingSMS(
        e,
        phoneNumbers,
        message,
        "안드로이드에서 전송 실패",
      );
    }
  }

  Future<bool> _handleFailCaseOfSendingSMS(
    Object e,
    List<String> phoneNumbers,
    String message,
    String debugMessage,
  ) async {
    debugPrint('$debugMessage: $e');
    return await _sendMultipleSmsOnServer(
      phoneNumbers: phoneNumbers,
      message: message,
    );
  }

  Future<bool> _sendSMSViaAndroidMethodChannel(
    List<String> phoneNumbers,
    String message,
  ) async {
    final result = await _channel.invokeMethod<bool>('sendSms', {
      'phoneNumbers': phoneNumbers,
      'message': message,
    });
    return result ?? false;
  }

  /// iOS에서 SMS 전송 시도 -> 지원 X
  Future<bool> _sendSmsIOS(List<String> phoneNumbers, String message) async {
    return await _sendMultipleSmsOnServer(
      phoneNumbers: phoneNumbers,
      message: message,
    );
  }

  Future<bool> sendSmsToMultipleRecipients({
    required List<String> phoneNumbers,
    required String message,
  }) async {
    return await sendSms(phoneNumbers: phoneNumbers, message: message);
  }

  Future<bool> _sendSingleSmsOnServer({
    required String phoneNumber,
    required String message,
  }) async {
    final dio = ref.read(dioProvider);

    try {
      const apiPath = '/api/v1/message';

      final response = await dio.post(
        apiPath,
        data: {'message': message, 'recieverNumber': phoneNumber},
      );

      final httpStatusCode = response.statusCode;

      return (httpStatusCode == 200 || httpStatusCode == 201);
    } catch (e) {
      debugPrint("서버를 통한 메시지 요청 시도 실패");
      return false;
    }
  }

  Future<bool> _sendMultipleSmsOnServer({
    required List<String> phoneNumbers,
    required String message,
  }) async {
    bool status = true;
    for (int i = 0; i < phoneNumbers.length; i++) {
      var bool = await _sendSingleSmsOnServer(
        phoneNumber: phoneNumbers[i],
        message: message,
      );

      if (bool == false) {
        status = false;
      }
    }

    return status;
  }
}
