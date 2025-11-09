import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/auth/login_button.dart';
import 'package:iamhere/common/view_component/auth/right_content_widget.dart';

class AuthView extends StatelessWidget {
  final String _appTitle = 'Imhere';
  final String _subTitle = '정해진 장소를 지나면 문자를 보낼게요!';
  final String _authorizationRequestDescription = '앱 사용을 위해 다음 권한이 필요해요.';
  final List<String> _authorizationRequests = [
    '위치',
    'SMS',
    '연락처',
    '백그라운드 위치',
  ];

  AuthView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_appTitle, style: Theme.of(context).textTheme.headlineLarge?.copyWith(
              fontSize: MediaQuery.of(context).size.width * 0.14
            )),
            SizedBox(height: MediaQuery.of(context).size.height * 0.01),
            Text(_subTitle, style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              fontSize: MediaQuery.of(context).size.width * 0.05
            ),),
            SizedBox(height: MediaQuery.of(context).size.height * 0.07),
            kakaoLoginButton(context: context, onPressed: (){}),
            SizedBox(height: MediaQuery.of(context).size.height * 0.02),
            googleLoginButton(context: context, onPressed: (){}),

            SizedBox(height: MediaQuery.of(context).size.height * 0.1),
            Text(_authorizationRequestDescription, style: Theme.of(context).textTheme.bodyLarge),
            SizedBox(height: MediaQuery.of(context).size.height * 0.01),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: _authorizationRequests.map((right) => rightContentWidget(context: context, right: right)).toList()
            )
          ],
        ),
      ),
    );
  }
}
