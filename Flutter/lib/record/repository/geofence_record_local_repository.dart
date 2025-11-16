import 'package:iamhere/common/database/database_service.dart';
import 'package:iamhere/record/repository/geofence_record_entity.dart';
import 'package:iamhere/record/repository/geofence_record_repository.dart';

class GeofenceRecordLocalRepository implements GeofenceRecordRepository {
  final DatabaseService _database = DatabaseService.instance;

  @override
  Future<List<GeofenceRecordEntity>> findAll() async {
    return await _database.findAllGeofenceRecords();
  }

  @override
  Future<List<GeofenceRecordEntity>> findAllOrderByCreatedAtDesc() async {
    return await _database.findAllGeofenceRecordsOrderByCreatedAtDesc();
  }

  @override
  Future<GeofenceRecordEntity> save(GeofenceRecordEntity entity) async {
    return await _database.saveGeofenceRecord(entity);
  }

  @override
  Future<void> delete(int id) async {
    await _database.deleteGeofenceRecord(id);
  }

  @override
  Future<void> deleteAll() async {
    await _database.deleteAllGeofenceRecords();
  }
}
