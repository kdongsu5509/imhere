import 'package:iamhere/contact/repository/contact_entity.dart';
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

  Future<Database> _initDatabase() async {
    String path = join(await getDatabasesPath(), databaseName);

    String contactTableQuery = _createContactstabeTableQuery();
    String geofenceTableQuery = _createGeofenceTableQuery();
    String recordsTableQuery = _createRecordsTableQuery();

    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        await db.execute(contactTableQuery);
        await db.execute(geofenceTableQuery);
        await db.execute(recordsTableQuery);
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

  String _createContactstabeTableQuery() {
    return 'CREATE TABLE $contactTableName(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number TEXT)';
  }

  String _createGeofenceTableQuery() {
    return 'CREATE TABLE $geofenceTableName(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lat REAL, lng REAL)';
  }

  String _createRecordsTableQuery() {
    return 'CREATE TABLE $recordTableName(id INTEGER PRIMARY KEY AUTOINCREMENT)';
  }
}
