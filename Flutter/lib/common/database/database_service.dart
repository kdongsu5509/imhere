import 'package:iamhere/contact/repository/contact_entity.dart';
import 'package:iamhere/geofence/repository/geofence_entity.dart';
import 'package:iamhere/record/repository/geofence_record_entity.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

class DatabaseService {
  static final DatabaseService instance = DatabaseService._init();
  static Database? _database;

  static const String databaseName = "im_here.db";
  static const String contactTableName = 'contacts';
  static const String geofenceTableName = 'geofence';
  static const String recordTableName = 'records';

  DatabaseService._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDatabase();
    return _database!;
  }

  String _createContactsTableQuery() {
    return 'CREATE TABLE $contactTableName(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number TEXT)';
  }

  String _createGeofenceTableQuery() {
    return 'CREATE TABLE $geofenceTableName(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lat REAL, lng REAL, radius REAL, message TEXT, contact_ids TEXT, is_active INTEGER DEFAULT 0)';
  }

  String _createRecordsTableQuery() {
    return 'CREATE TABLE $recordTableName(id INTEGER PRIMARY KEY AUTOINCREMENT, geofence_id INTEGER, geofence_name TEXT, message TEXT, recipients TEXT, created_at TEXT, send_machine TEXT)';
  }

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), databaseName);

    String contactTableQuery = _createContactsTableQuery();
    String geofenceTableQuery = _createGeofenceTableQuery();
    String recordsTableQuery = _createRecordsTableQuery();

    return await openDatabase(
      path,
      version: 3, // 버전 업그레이드 (send_machine 컬럼 추가)
      onCreate: (db, version) async {
        await db.execute(contactTableQuery);
        await db.execute(geofenceTableQuery);
        await db.execute(recordsTableQuery);
      },
      onUpgrade: (db, oldVersion, newVersion) async {
        if (oldVersion < 2) {
          // 버전 1에서 2로 업그레이드: geofence와 records 테이블 재생성
          // 기존 geofence 테이블 백업 (선택사항)
          await db.execute('DROP TABLE IF EXISTS ${geofenceTableName}_backup');
          await db.execute(
            'CREATE TABLE ${geofenceTableName}_backup AS SELECT * FROM $geofenceTableName',
          );

          // 기존 테이블 삭제
          await db.execute('DROP TABLE IF EXISTS $geofenceTableName');
          await db.execute('DROP TABLE IF EXISTS $recordTableName');

          // 새로운 스키마로 재생성
          await db.execute(geofenceTableQuery);
          await db.execute(recordsTableQuery);

          // 백업에서 데이터 복원 시도 (id, name, lat, lng만 복원)
          try {
            await db.execute('''
              INSERT INTO $geofenceTableName (id, name, lat, lng, radius, message, contact_ids, is_active)
              SELECT id, name, lat, lng, 100.0, '', '[]', 0
              FROM ${geofenceTableName}_backup
            ''');
            await db.execute(
              'DROP TABLE IF EXISTS ${geofenceTableName}_backup',
            );
          } catch (e) {
            // 백업 테이블이 없거나 복원 실패 시 무시
            await db.execute(
              'DROP TABLE IF EXISTS ${geofenceTableName}_backup',
            );
          }
        }
        if (oldVersion < 3) {
          // 버전 2에서 3으로 업그레이드: records 테이블에 send_machine 컬럼 추가
          try {
            await db.execute(
              'ALTER TABLE $recordTableName ADD COLUMN send_machine TEXT DEFAULT "MOBILE"',
            );
          } catch (e) {
            // 컬럼이 이미 존재하거나 오류 발생 시 무시
          }
        }
      },
    );
  }

  /**
   * 연락처
   * - Create - save : 1개 저장
   * - Read - findAll : 전체 조회
   * - Delete - delete : 아이디로 조회 후 삭제
   */
  ///
  // C
  Future<ContactEntity> saveContact(ContactEntity entity) async {
    var db = await instance.database;
    final id = await db.insert(
      contactTableName,
      entity.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );

    return ContactEntity(id: id, name: entity.name, number: entity.number);
  }

  // Read All
  Future<List<ContactEntity>> findAllContacts() async {
    var db = await instance.database;
    const orderBy = 'name ASC';
    final result = await db.query(contactTableName, orderBy: orderBy);
    return result.map((json) => ContactEntity.fromMap(json)).toList();
  }

  //Delete
  Future<void> deleteContact(int id) async {
    var db = await instance.database;
    await db.delete(contactTableName, where: 'id = ?', whereArgs: [id]);
  }

  /**
   * 지오펜스
   * - Create - save : 1개 저장
   * - Read - findAll : 전체 조회
   * - Delete - delete : 아이디로 조회 후 삭제
   */
  ///
  // C
  Future<GeofenceEntity> saveGeofence(GeofenceEntity entity) async {
    var db = await instance.database;
    final id = await db.insert(
      geofenceTableName,
      entity.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );

    return GeofenceEntity(
      id: id,
      name: entity.name,
      lat: entity.lat,
      lng: entity.lng,
      radius: entity.radius,
      message: entity.message,
      contactIds: entity.contactIds,
    );
  }

  // Read All
  Future<List<GeofenceEntity>> findAllGeofences() async {
    var db = await instance.database;
    const orderBy = 'name ASC';
    final result = await db.query(geofenceTableName, orderBy: orderBy);
    return result.map((json) => GeofenceEntity.fromMap(json)).toList();
  }

  //Delete
  Future<void> deleteGeofence(int id) async {
    var db = await instance.database;
    await db.delete(geofenceTableName, where: 'id = ?', whereArgs: [id]);
  }

  // Update isActive
  Future<void> updateGeofenceActiveStatus(int id, bool isActive) async {
    var db = await instance.database;
    await db.update(
      geofenceTableName,
      {'is_active': isActive ? 1 : 0},
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  /**
   * 지오펜스 기록
   * - Create - save : 1개 저장
   * - Read - findAll : 전체 조회
   * - Delete - delete : 아이디로 조회 후 삭제
   */
  ///
  // C
  Future<GeofenceRecordEntity> saveGeofenceRecord(
    GeofenceRecordEntity entity,
  ) async {
    var db = await instance.database;
    final id = await db.insert(
      recordTableName,
      entity.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );

    return GeofenceRecordEntity(
      id: id,
      geofenceId: entity.geofenceId,
      geofenceName: entity.geofenceName,
      message: entity.message,
      recipients: entity.recipients,
      createdAt: entity.createdAt,
      sendMachine: entity.sendMachine,
    );
  }

  // Read All
  Future<List<GeofenceRecordEntity>> findAllGeofenceRecords() async {
    var db = await instance.database;
    const orderBy = 'created_at DESC';
    final result = await db.query(recordTableName, orderBy: orderBy);
    return result.map((json) => GeofenceRecordEntity.fromMap(json)).toList();
  }

  // Read All (최신순)
  Future<List<GeofenceRecordEntity>>
  findAllGeofenceRecordsOrderByCreatedAtDesc() async {
    return await findAllGeofenceRecords();
  }

  //Delete
  Future<void> deleteGeofenceRecord(int id) async {
    var db = await instance.database;
    await db.delete(recordTableName, where: 'id = ?', whereArgs: [id]);
  }

  //Delete All
  Future<void> deleteAllGeofenceRecords() async {
    var db = await instance.database;
    await db.delete(recordTableName);
  }
}
