import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/auth/service/token_storage_service.dart';
import 'package:iamhere/auth/view/component/login_button.dart';
import 'package:iamhere/auth/view/component/login_button_info.dart';
import 'package:iamhere/auth/view/component/right_content_widget.dart';
import 'package:iamhere/auth/view_model/auth_view_model_provider.dart';
import 'package:iamhere/common/router/go_router.dart';

class AuthView extends ConsumerStatefulWidget {
  const AuthView({super.key});

  @override
  ConsumerState<AuthView> createState() => _AuthViewState();
}

class _AuthViewState extends ConsumerState<AuthView> {
  final String _appTitle = 'Imhere';
  final String _subTitle = '정해진 장소를 지나면 문자를 보낼게요!';
  final String _authorizationRequestDescription = '앱 사용을 위해 다음 권한이 필요해요';
  final List<String> _authorizationElements = ['위치', 'SMS', '연락처', '백그라운드 위치'];

  Future<void> _handleLogin() async {
    final viewModel = ref.read(authViewModelInterfaceProvider);
    await viewModel.handleKakaoLogin();
    
    // 로그인 성공 후 토큰 확인 및 화면 전환
    final accessToken = await TokenStorageService().getAccessToken();
    if (accessToken != null && accessToken.isNotEmpty && mounted) {
      // 라우터를 갱신하여 ShellRoute의 builder가 다시 실행되도록 함
      if (context.mounted) {
        // router.refresh()를 사용하여 라우터를 새로고침
        router.refresh();
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            SizedBox(height: 65.h),
            buildAppTitle(context),
            SizedBox(height: 6.h),
            buildAppSubTitle(context),
            SizedBox(height: 270.h),
            consistLoginButtons(context, ref),
            SizedBox(height: 40.h),
            buildAuthorizationRequestDescription(context),
            SizedBox(height: 10.h),
            consistAuthenticationElements(context),
          ],
        ),
      ),
    );
  }

  Text buildAppTitle(BuildContext context) {
    return Text(
      _appTitle,
      style: Theme.of(
        context,
      ).textTheme.headlineLarge?.copyWith(fontSize: 58.sp),
    );
  }

  Text buildAppSubTitle(BuildContext context) {
    return Text(
      _subTitle,
      style: Theme.of(
        context,
      ).textTheme.headlineMedium?.copyWith(fontSize: 20.sp),
    );
  }

  Widget consistLoginButtons(BuildContext context, WidgetRef ref) {
    return Column(
      children: [
        LoginButton(
          buttonInfo: LoginInfoData.kakao,
          onPressed: _handleLogin,
        ),
      ],
    );
  }

  Text buildAuthorizationRequestDescription(BuildContext context) {
    return Text(
      _authorizationRequestDescription,
      style: Theme.of(context).textTheme.bodyLarge,
    );
  }

  Row consistAuthenticationElements(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: _authorizationElements
          .map((right) => rightContentWidget(context: context, right: right))
          .toList(),
    );
  }
}
