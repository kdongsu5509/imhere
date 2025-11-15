import 'contact.dart';

abstract class ContactViewModelInterface {
  Future<List<Contact>> importContact();
  Future<Contact?> selectContact();
}
