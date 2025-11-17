import 'package:dio/dio.dart';
import 'package:iamhere/auth/model/auth_service_interface.dart';
import 'package:iamhere/auth/service/token_storage_service.dart';
import 'package:riverpod/src/framework.dart';

import '../../common/external_dio/dio_provider.dart';

class AuthService implements AuthServiceInterface {
  final Ref _ref;
  final Dio _dio;

  AuthService(this._ref) : _dio = _ref.read(dioProvider);

  @override
  sendIdTokenToServer(String idToken) async {
    try {
      final response = await _dio.post(
        '/api/v1/auth/login',
        data: {'provider': 'KAKAO', 'idToken': idToken},
      );

      // 서버 응답에서 JWT 토큰 추출 및 저장
      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = response.data;
        final accessToken = data['accessToken'] as String?;
        final refreshToken = data['refreshToken'] as String?;

        if (accessToken != null) {
          await TokenStorageService().saveAccessToken(accessToken);
        }

        if (refreshToken != null) {
          await TokenStorageService().saveRefreshToken(refreshToken);
        }
      }
    } on DioException catch (e) {
      throw Exception('자체 백엔드 서버에 토큰 전송 실패');
    }
  }
}
