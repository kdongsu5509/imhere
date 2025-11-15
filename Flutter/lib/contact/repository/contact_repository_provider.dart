import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:iamhere/contact/repository/contact_local_repository.dart';
import 'package:iamhere/contact/repository/contact_repository.dart';

part 'contact_repository_provider.g.dart';

@Riverpod(keepAlive: true)
ContactRepository contactRepository(Ref ref) {
  return ContactLocalRepository();
}
