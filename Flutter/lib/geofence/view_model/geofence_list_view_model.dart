import 'package:iamhere/geofence/repository/geofence_entity.dart';
import 'package:iamhere/geofence/repository/geofence_repository_provider.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'geofence_list_view_model.g.dart';

@Riverpod(keepAlive: true)
class GeofenceListViewModel extends _$GeofenceListViewModel {
  @override
  Future<List<GeofenceEntity>> build() async {
    final repository = ref.read(geofenceRepositoryProvider);
    return await repository.findAll();
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      final repository = ref.read(geofenceRepositoryProvider);
      return await repository.findAll();
    });
  }

  Future<void> toggleActive(int id, bool isActive) async {
    if (!state.hasValue) return;

    final previousState = state;
    final originalList = state.value!;

    try {
      // 낙관적 업데이트
      final updatedList = originalList.map((geofence) {
        if (geofence.id == id) {
          return geofence.copyWith(isActive: isActive);
        }
        return geofence;
      }).toList();
      state = AsyncValue.data(updatedList);

      // 데이터베이스 업데이트
      final repository = ref.read(geofenceRepositoryProvider);
      await repository.updateActiveStatus(id, isActive);
    } catch (e) {
      // 실패 시 롤백
      state = previousState;
      rethrow;
    }
  }
}
