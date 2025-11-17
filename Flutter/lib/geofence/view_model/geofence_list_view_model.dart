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
      // 낙관적 업데이트: 새로운 리스트 생성하여 상태 업데이트
      final updatedList = originalList.map((geofence) {
        if (geofence.id == id) {
          // 명시적으로 isActive를 전달하여 false도 올바르게 처리
          return geofence.copyWith(isActive: isActive);
        }
        return geofence;
      }).toList();
      
      // 상태를 새로운 리스트로 업데이트 (새로운 객체 참조로 리빌드 트리거)
      state = AsyncValue.data(List.from(updatedList));

      // 데이터베이스 업데이트
      final repository = ref.read(geofenceRepositoryProvider);
      await repository.updateActiveStatus(id, isActive);
      
      // 데이터베이스 업데이트 후 상태를 다시 확인하여 동기화
      // 낙관적 업데이트가 성공했으므로 추가 리프레시는 필요 없지만,
      // 혹시 모를 동기화 문제를 방지하기 위해 선택적으로 추가 가능
    } catch (e) {
      // 실패 시 롤백
      state = previousState;
      rethrow;
    }
  }

  Future<void> delete(int id) async {
    if (!state.hasValue) return;

    final previousState = state;
    final originalList = state.value!;

    try {
      // 낙관적 업데이트: 삭제할 항목 제외
      final updatedList = originalList.where((geofence) => geofence.id != id).toList();
      state = AsyncValue.data(updatedList);

      // 데이터베이스에서 삭제
      final repository = ref.read(geofenceRepositoryProvider);
      await repository.delete(id);
    } catch (e) {
      // 실패 시 롤백
      state = previousState;
      rethrow;
    }
  }
}
