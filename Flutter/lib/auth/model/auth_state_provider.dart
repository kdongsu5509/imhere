import 'package:iamhere/auth/service/token_storage_service.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'auth_state_provider.g.dart';

/// 인증 상태를 확인하는 Provider
/// Access Token이 있으면 true, 없으면 false를 반환
@Riverpod(keepAlive: true)
Future<bool> authState(Ref ref) async {
  final accessToken = await TokenStorageService().getAccessToken();
  return accessToken != null && accessToken.isNotEmpty;
}

