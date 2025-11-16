import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:iamhere/geofence/repository/geofence_local_repository.dart';
import 'package:iamhere/geofence/repository/geofence_repository.dart';

part 'geofence_repository_provider.g.dart';

@Riverpod(keepAlive: true)
GeofenceRepository geofenceRepository(Ref ref) {
  return GeofenceLocalRepository();
}
