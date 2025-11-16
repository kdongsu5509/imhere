import 'package:dio/dio.dart';
import 'package:iamhere/common/external_dio/auth_interceptor.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'dio_provider.g.dart';

@Riverpod(keepAlive: true)
Dio dio(Ref ref) {
  final dio = Dio(
    BaseOptions(
      baseUrl: 'http://10.0.0.198:8080',
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {'Content-Type': 'application/json'},
    ),
  );

  // 인증 인터셉터 추가 (먼저 추가하여 토큰 자동 추가 및 refresh 처리)
  dio.interceptors.add(AuthInterceptor(dio));

  // 로그 인터셉터 추가
  dio.interceptors.add(
    LogInterceptor(
      //요청
      request: true,
      requestHeader: true,
      requestBody: true,
      //응답
      responseHeader: true,
      responseBody: true,
      //에러
      error: true,
    ),
  );

  return dio;
}
