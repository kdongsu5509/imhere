import 'package:dio/dio.dart';
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
