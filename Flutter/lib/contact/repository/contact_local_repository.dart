import 'package:iamhere/common/database/database_service.dart';
import 'package:iamhere/contact/repository/contact_entity.dart';
import 'package:iamhere/contact/repository/contact_repository.dart';

class ContactLocalRepository implements ContactRepository {
  final DatabaseService _database = DatabaseService.instance;

  @override
  Future<List<ContactEntity>> findAll() async {
    return await _database.findAllContacts();
  }

  @override
  Future<ContactEntity> save(ContactEntity entity) async {
    return await _database.saveContact(entity);
  }

  @override
  Future<void> delete(int id) async {
    await _database.deleteContact(id);
  }
}
