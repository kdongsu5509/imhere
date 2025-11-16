import 'package:iamhere/record/repository/geofence_record_entity.dart';
import 'package:iamhere/record/repository/geofence_record_repository_provider.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'geofence_record_view_model.g.dart';

@Riverpod(keepAlive: true)
class GeofenceRecordViewModel extends _$GeofenceRecordViewModel {
  @override
  Future<List<GeofenceRecordEntity>> build() async {
    final repository = ref.read(geofenceRecordRepositoryProvider);
    return await repository.findAllOrderByCreatedAtDesc();
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      final repository = ref.read(geofenceRecordRepositoryProvider);
      return await repository.findAllOrderByCreatedAtDesc();
    });
  }
}
