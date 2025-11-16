import 'package:permission_handler/permission_handler.dart';

/// SMS 권한 관리 서비스
class SmsPermissionService {
  /// SMS 권한 상태 확인
  Future<PermissionStatus> getSmsPermissionStatus() async {
    return await Permission.sms.status;
  }

  /// SMS 권한 요청
  Future<PermissionStatus> requestSmsPermission() async {
    final status = await Permission.sms.request();
    return status;
  }

  /// SMS 권한이 허용되었는지 확인
  Future<bool> isSmsPermissionGranted() async {
    final status = await Permission.sms.status;
    return status.isGranted;
  }

  /// SMS 권한 요청 및 확인
  /// 권한이 없으면 요청하고, 최종 상태를 반환합니다.
  Future<bool> requestAndCheckSmsPermission() async {
    final currentStatus = await Permission.sms.status;

    if (currentStatus.isGranted) {
      return true;
    }

    if (currentStatus.isDenied) {
      final requestedStatus = await Permission.sms.request();
      return requestedStatus.isGranted;
    }

    if (currentStatus.isPermanentlyDenied) {
      // 영구적으로 거부된 경우 설정으로 이동
      await openAppSettings();
      return false;
    }

    return false;
  }
}
