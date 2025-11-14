import 'package:riverpod_annotation/riverpod_annotation.dart';

import 'contact_view_model.dart';
import 'contact_view_model_interface.dart';

part 'contact_view_model_provider.g.dart';

@Riverpod(keepAlive: true)
ContactViewModel contactViewModel(Ref ref) {
  return ContactViewModel();
}

@Riverpod(keepAlive: true)
ContactViewModelInterface contactViewModelInterface(Ref ref) {
  return ref.watch(contactViewModelProvider);
}
