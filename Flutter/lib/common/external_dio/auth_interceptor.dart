import 'package:dio/dio.dart';
import 'package:iamhere/auth/service/token_storage_service.dart';

/// 인증 인터셉터
/// - 요청 시 자동으로 Access Token을 헤더에 추가
/// - 401 에러 발생 시 자동으로 Refresh Token으로 재발급 요청
class AuthInterceptor extends Interceptor {
  final Dio _dio;
  bool _isRefreshing = false;
  final List<_PendingRequest> _pendingRequests = [];

  AuthInterceptor(this._dio);

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    // refresh 요청은 토큰 추가하지 않음 (순환 참조 방지)
    if (options.path == '/api/v1/auth/refresh') {
      handler.next(options);
      return;
    }

    // Access Token이 있으면 헤더에 추가
    final accessToken = await TokenStorageService().getAccessToken();
    if (accessToken != null) {
      options.headers['Authorization'] = 'Bearer $accessToken';
    }

    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    // 401 Unauthorized 에러인 경우
    if (err.response?.statusCode == 401) {
      final requestOptions = err.requestOptions;

      // refresh 요청 자체가 401이면 토큰 삭제하고 실패 처리
      if (requestOptions.path == '/api/v1/auth/refresh') {
        await TokenStorageService().deleteAllTokens();
        handler.next(err);
        return;
      }

      // 이미 refresh 중이면 대기
      if (_isRefreshing) {
        return _addPendingRequest(requestOptions, handler);
      }

      _isRefreshing = true;

      try {
        // Refresh Token으로 새 Access Token 발급
        final newAccessToken = await _refreshAccessToken();

        if (newAccessToken != null) {
          // 새 Access Token으로 원래 요청 재시도
          final opts = requestOptions;
          opts.headers['Authorization'] = 'Bearer $newAccessToken';

          // 원래 요청 재시도
          try {
            final response = await _dio.fetch(opts);
            handler.resolve(response);
          } catch (e) {
            handler.reject(DioException(requestOptions: opts, error: e));
          }

          // 대기 중인 요청들 처리
          _processPendingRequests(newAccessToken);
        } else {
          // Refresh 실패 - 로그아웃 처리 필요
          await TokenStorageService().deleteAllTokens();
          _rejectPendingRequests(err);
          handler.reject(err);
        }
      } catch (e) {
        // Refresh 실패
        await TokenStorageService().deleteAllTokens();
        _rejectPendingRequests(err);
        handler.reject(err);
      } finally {
        _isRefreshing = false;
        _pendingRequests.clear();
      }
    } else {
      handler.next(err);
    }
  }

  /// Refresh Token으로 새 Access Token 발급
  Future<String?> _refreshAccessToken() async {
    try {
      final refreshToken = await TokenStorageService().getRefreshToken();

      if (refreshToken == null) {
        return null;
      }

      // 순환 참조 방지를 위해 인터셉터 없는 새 Dio 인스턴스 사용
      final refreshDio = Dio(
        BaseOptions(
          baseUrl: _dio.options.baseUrl,
          connectTimeout: _dio.options.connectTimeout,
          receiveTimeout: _dio.options.receiveTimeout,
          headers: {'Content-Type': 'application/json'},
        ),
      );

      final response = await refreshDio.post(
        '/api/v1/auth/refresh',
        data: {'refreshToken': refreshToken},
      );

      if (response.statusCode == 200) {
        final data = response.data;
        final newAccessToken = data['accessToken'] as String?;
        final newRefreshToken = data['refreshToken'] as String?;

        if (newAccessToken != null) {
          await TokenStorageService().saveAccessToken(newAccessToken);
        }

        if (newRefreshToken != null) {
          await TokenStorageService().saveRefreshToken(newRefreshToken);
        }

        return newAccessToken;
      }

      return null;
    } catch (e) {
      return null;
    }
  }

  /// 대기 중인 요청 추가
  void _addPendingRequest(
    RequestOptions requestOptions,
    ErrorInterceptorHandler handler,
  ) {
    _pendingRequests.add(_PendingRequest(requestOptions, handler));
  }

  /// 대기 중인 요청들 처리
  void _processPendingRequests(String newAccessToken) {
    for (final pending in _pendingRequests) {
      try {
        pending.requestOptions.headers['Authorization'] =
            'Bearer $newAccessToken';
        _dio
            .fetch(pending.requestOptions)
            .then((response) {
              pending.handler.resolve(response);
            })
            .catchError((e) {
              pending.handler.reject(
                DioException(requestOptions: pending.requestOptions, error: e),
              );
            });
      } catch (e) {
        pending.handler.reject(
          DioException(requestOptions: pending.requestOptions, error: e),
        );
      }
    }
  }

  /// 대기 중인 요청들 모두 실패 처리
  void _rejectPendingRequests(DioException err) {
    for (final pending in _pendingRequests) {
      pending.handler.reject(err);
    }
  }
}

/// 대기 중인 요청 정보
class _PendingRequest {
  final RequestOptions requestOptions;
  final ErrorInterceptorHandler handler;

  _PendingRequest(this.requestOptions, this.handler);
}
