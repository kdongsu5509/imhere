import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:iamhere/record/repository/geofence_record_local_repository.dart';
import 'package:iamhere/record/repository/geofence_record_repository.dart';

part 'geofence_record_repository_provider.g.dart';

@Riverpod(keepAlive: true)
GeofenceRecordRepository geofenceRecordRepository(Ref ref) {
  return GeofenceRecordLocalRepository();
}
