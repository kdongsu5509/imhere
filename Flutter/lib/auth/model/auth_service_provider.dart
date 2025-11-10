import 'package:iamhere/auth/model/auth_service.dart';
import 'package:iamhere/auth/model/auth_service_interface.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'auth_service_provider.g.dart';

@riverpod
AuthService authService(Ref ref) {
  return AuthService(ref);
}

// UI/ViewModel이 사용할 IAuthService 인터페이스 Provider
@riverpod
IAuthService authServiceInterface(Ref ref) {
  return ref.watch(authServiceProvider);
}
