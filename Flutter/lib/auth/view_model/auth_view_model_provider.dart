import 'package:riverpod_annotation/riverpod_annotation.dart';

import 'auth_view_model.dart';
import 'auth_view_model_interface.dart';

part 'auth_view_model_provider.g.dart';

@Riverpod(keepAlive: true)
AuthViewModel authViewModel(Ref ref) {
  return AuthViewModel(ref);
}

@Riverpod(keepAlive: true)
AuthViewModelInterface authViewModelInterface(Ref ref) {
  return ref.watch(authViewModelProvider);
}
