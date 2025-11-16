import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';

// 권한 상태 Enum을 정의하여 현재 상태를 명확히 반환합니다.
enum LocationPermissionState {
  grantedAlways, // 항상 허용됨
  grantedWhenInUse, // 앱 사용 중에만 허용됨
  denied, // 거부됨
  permanentlyDenied, // 영구적으로 거부됨
  restricted, // 제한됨
}

class MyLocationService {
  // 1. getCurrentUserLocation: 최종적으로 위치를 가져오거나 예외를 던집니다.
  Future<Position> getCurrentUserLocation() async {
    // 권한 체크 및 요청 로직을 분리하여 상태를 반환받습니다.
    LocationPermissionState status = await requestLocationPermissions();

    // 항상 허용 또는 앱 사용 중에만 허용된 경우에만 위치를 가져옵니다.
    if (status == LocationPermissionState.grantedAlways ||
        status == LocationPermissionState.grantedWhenInUse) {
      final LocationSettings locationSettings = LocationSettings(
        accuracy: LocationAccuracy.high,
        distanceFilter: 100,
      );
      return await Geolocator.getCurrentPosition(
        locationSettings: locationSettings,
      );
    }

    // 그 외 상태는 예외를 던지도록 처리합니다.
    throw Exception("위치 권한이 충분히 허용되지 않았습니다. 현재 상태: ${status.name}");
  }

  // 2. requestLocationPermissions: 권한 요청 로직을 2단계로 처리하고 최종 상태를 반환합니다.
  Future<LocationPermissionState> requestLocationPermissions() async {
    // 2-1. 첫 번째: '앱 사용 중에만 허용' 권한 요청 (팝업으로 뜸)
    PermissionStatus whenInUseStatus = await Permission.locationWhenInUse
        .request();

    if (whenInUseStatus.isGranted || whenInUseStatus.isLimited) {
      // 2-2. 첫 번째 권한이 허용되었다면, '항상 허용' 권한 상태를 확인합니다.
      PermissionStatus alwaysStatus = await Permission.locationAlways.status;

      if (alwaysStatus.isGranted) {
        // 이미 '항상 허용' 상태라면 바로 종료
        return LocationPermissionState.grantedAlways;
      }

      if (alwaysStatus.isDenied) {
        // 2-3. '항상 허용'이 거부된 상태라면 (안드로이드에서 기본 상태),
        // 사용자에게 설정으로 이동하도록 안내합니다.

        /* * 📌 중요: 이 시점에서 Flutter UI (AlertDialog 등)를 통해
         * 사용자에게 "지오펜싱을 위해 설정에서 '항상 허용'을 선택해주세요" 라고 안내해야 합니다.
         * 이 서비스 클래스는 UI 로직을 포함할 수 없으므로,
         * 호출하는 쪽(Widget)에서 이 상태를 확인하고 안내해야 합니다.
         */

        // 설정을 열어줍니다. (사용자가 수동으로 '항상 허용'을 선택하도록 유도)
        await openAppSettings();

        // 사용자가 설정에서 돌아왔으므로, 다시 최종 상태를 확인합니다.
        alwaysStatus = await Permission.locationAlways.status;
      }

      // 2-4. 최종 상태 반환
      if (alwaysStatus.isGranted) {
        return LocationPermissionState.grantedAlways;
      } else {
        // '항상 허용'이 안 되었지만, '앱 사용 중에만 허용'은 된 상태
        return LocationPermissionState.grantedWhenInUse;
      }
    }

    // 2-5. '앱 사용 중에만 허용'조차 거부된 경우
    if (whenInUseStatus.isDenied) {
      return LocationPermissionState.denied;
    }
    if (whenInUseStatus.isPermanentlyDenied) {
      return LocationPermissionState.permanentlyDenied;
    }
    if (whenInUseStatus.isRestricted) {
      return LocationPermissionState.restricted;
    }

    // 모든 예외를 처리하지 못하는 경우 (거의 없음)
    return LocationPermissionState.denied;
  }
}
