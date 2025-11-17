import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:iamhere/auth/model/auth_service_provider.dart';
import 'package:iamhere/auth/view_model/auth_view_model_interface.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';

class AuthViewModel implements AuthViewModelInterface {
  final Ref _ref;

  AuthViewModel(this._ref) : super();

  @override
  Future<void> handleKakaoLogin() async {
    final authService = _ref.read(authServiceInterfaceProvider);
    String? idToken;
    idToken = await _doUserKakaoLogin();
    if (idToken != null) {
      await authService.sendIdTokenToServer(idToken);
    }
  }

  Future<String?> _doUserKakaoLogin() async {
    if (await isKakaoTalkInstalled()) {
      return await _loginWithKakaoTalkApplication();
    }

    return await _loginWithKakaoAccountOnWebPopUp();
  }

  Future<String?> _loginWithKakaoTalkApplication() async {
    try {
      OAuthToken oAuthToken = await UserApi.instance.loginWithKakaoTalk();
      log('카카오톡으 어플리케이션으로 로그인 성공');
      return oAuthToken.idToken;
    } catch (error) {
      log('카카오톡 어플리케이션으로 로그인 실패 $error');
      if (error is PlatformException && error.code == 'CANCELED') {
        log('의도적인 취소로 인한 실패');
      }
      return null;
    }
  }

  Future<String?> _loginWithKakaoAccountOnWebPopUp() async {
    try {
      OAuthToken oAuthToken = await UserApi.instance.loginWithKakaoAccount();
      log('팝업 화면에서 카카오계정으로 로그인 성공');
      return oAuthToken.idToken;
    } catch (error) {
      log('팝업 화면에서 카카오계정으로 로그인 실패 $error');
      return null;
    }
  }
}
