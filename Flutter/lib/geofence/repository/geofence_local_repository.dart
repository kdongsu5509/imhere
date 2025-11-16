import 'package:iamhere/common/database/database_service.dart';
import 'package:iamhere/geofence/repository/geofence_entity.dart';
import 'package:iamhere/geofence/repository/geofence_repository.dart';

class GeofenceLocalRepository implements GeofenceRepository {
  final DatabaseService _database = DatabaseService.instance;

  @override
  Future<List<GeofenceEntity>> findAll() async {
    return await _database.findAllGeofences();
  }

  @override
  Future<GeofenceEntity> save(GeofenceEntity entity) async {
    return await _database.saveGeofence(entity);
  }

  @override
  Future<void> delete(int id) async {
    await _database.deleteGeofence(id);
  }

  @override
  Future<void> updateActiveStatus(int id, bool isActive) async {
    await _database.updateGeofenceActiveStatus(id, isActive);
  }
}
