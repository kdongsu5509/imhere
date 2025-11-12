import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/auth/view/component/login_button.dart';
import 'package:iamhere/auth/view/component/login_button_info.dart';
import 'package:iamhere/auth/view/component/right_content_widget.dart';
import 'package:iamhere/auth/view_model/auth_view_model_provider.dart';

class AuthView extends ConsumerWidget {
  final String _appTitle = 'Imhere';
  final String _subTitle = '정해진 장소를 지나면 문자를 보낼게요!';
  final String _authorizationRequestDescription = '앱 사용을 위해 다음 권한이 필요해요';
  final List<String> _authorizationElements = ['위치', 'SMS', '연락처', '백그라운드 위치'];

  AuthView({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
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
    final viewModel = ref.read(authViewModelInterfaceProvider);
    return Column(
      children: [
        LoginButton(
          buttonInfo: LoginInfoData.kakao,
          onPressed: viewModel.handleKakaoLogin,
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
