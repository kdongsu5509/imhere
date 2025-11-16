import 'dart:convert';
import 'package:iamhere/geofence/repository/geofence_entity.dart';
import 'package:iamhere/geofence/repository/geofence_repository_provider.dart';
import 'package:iamhere/geofence/view_model/geofence_view_model_interface.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'geofence_view_model.g.dart';

@Riverpod(keepAlive: true)
class GeofenceViewModel extends _$GeofenceViewModel
    implements GeofenceViewModelInterface {
  late PermissionStatus alwaysStatus;

  @override
  Future<PermissionStatus> build() async {
    return await Permission.locationAlways.status;
  }

  Future<void> refreshPermissionStatus() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      return await Permission.locationAlways.status;
    });
  }

  @override
  Future<GeofenceEntity> saveGeofence({
    required String name,
    required double lat,
    required double lng,
    required double radius,
    required String message,
    required List<int> contactIds,
  }) async {
    final repository = ref.read(geofenceRepositoryProvider);

    // 연락처 ID 리스트를 JSON 문자열로 변환
    final contactIdsJson = jsonEncode(contactIds);

    final entity = GeofenceEntity(
      name: name,
      lat: lat,
      lng: lng,
      radius: radius,
      message: message,
      contactIds: contactIdsJson,
    );

    return await repository.save(entity);
  }

  @override
  Future<List<GeofenceEntity>> findAllGeofences() async {
    final repository = ref.read(geofenceRepositoryProvider);
    return await repository.findAll();
  }

  @override
  Future<void> toggleGeofenceActive(int id, bool isActive) async {
    final repository = ref.read(geofenceRepositoryProvider);
    await repository.updateActiveStatus(id, isActive);
  }
}
