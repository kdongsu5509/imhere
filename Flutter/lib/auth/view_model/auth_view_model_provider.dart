import 'package:riverpod_annotation/riverpod_annotation.dart';

import 'auth_view_model.dart';
import 'auth_view_model_interface.dart';

part 'auth_view_model_provider.g.dart';

@riverpod
AuthViewModel authViewModel(Ref ref) {
  return AuthViewModel(ref);
}

@riverpod
IAuthViewModel authViewModelInterface(Ref ref) {
  return ref.watch(authViewModelProvider);
}
