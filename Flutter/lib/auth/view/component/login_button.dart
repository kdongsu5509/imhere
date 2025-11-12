import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/auth/view/component/login_button_info.dart';

class LoginButton extends StatelessWidget {
  final LoginButtonInfo buttonInfo;
  final VoidCallback onPressed;
  const LoginButton({
    super.key,
    required this.buttonInfo,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    final Color borderColor = borderColorSelector();

    return ElevatedButton(
      onPressed: onPressed,
      style: buildLoginButtonStyle(borderColor),
      child: Center(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            loginButtonIcon(),
            SizedBox(width: 20.w),
            providerDescription(context),
            SizedBox(width: 5.w),
          ],
        ),
      ),
    );
  }

  Image loginButtonIcon() {
    return Image.asset(buttonInfo.assetAddress, width: 30.w, height: 30.h);
  }

  Text providerDescription(BuildContext context) {
    return Text(
      buttonInfo.description,
      style: Theme.of(
        context,
      ).textTheme.headlineMedium?.copyWith(fontSize: 20.sp),
    );
  }

  ButtonStyle buildLoginButtonStyle(Color borderColor) {
    final _width = 350.w;
    final _height = 50.h;

    return ElevatedButton.styleFrom(
      minimumSize: Size(_width, _height), // 최소 크기도 38.4로 설정
      maximumSize: Size(_width, _height),
      padding: EdgeInsets.zero,
      elevation: 0,
      backgroundColor: buttonInfo.backgroundColor,

      shape: buildRoundedRectangleBorder(borderColor),
    );
  }

  RoundedRectangleBorder buildRoundedRectangleBorder(Color borderColor) {
    return RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(35.0),
      side: BorderSide(
        color: borderColor,
        width: buttonInfo.border ? 1.0 : 0.0,
      ),
    );
  }

  Color borderColorSelector() {
    return buttonInfo.border
        ? Colors.grey.shade400
        : buttonInfo.backgroundColor;
  }
}
