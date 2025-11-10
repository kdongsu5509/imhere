import 'package:dio/dio.dart';
import 'package:iamhere/auth/model/auth_service_interface.dart';
import 'package:riverpod/src/framework.dart';

import '../../common/external_dio/dio_provider.dart';

class AuthService implements IAuthService {
  final Ref _ref;
  final Dio _dio;

  AuthService(this._ref) : _dio = _ref.read(dioProvider);

  @override
  sendIdTokenToServer(String idToken) async {
    try {
      await _dio.post('/auth/kakao', data: {'idToken': idToken});
    } on DioException catch (e) {
      throw Exception('자체 백엔드 서버에 토큰 전송 실패');
    }
  }
}
