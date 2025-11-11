import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/FlexibleScreen.dart';
import 'package:iamhere/common/view_component/auth/login_button_info.dart';

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
    final width = FlexibleScreen.getWidth(context);
    final height = FlexibleScreen.getHeight(context);

    final Color borderColor = borderColorSelector();

    return ElevatedButton(
      onPressed: onPressed,
      style: buildLoginButtonStyle(width, height, borderColor),
      child: Center(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            loginButtonIcon(height),
            SizedBox(width: width * 0.04),
            providerDescription(context, width),
            SizedBox(width: width * 0.02 + height * 0.02),
          ],
        ),
      ),
    );
  }

  Image loginButtonIcon(double height) {
    return Image.asset(
      buttonInfo.assetAddress,
      width: height * 0.02,
      height: height * 0.02,
    );
  }

  Text providerDescription(BuildContext context, double width) {
    return Text(
      buttonInfo.description,
      style: Theme.of(
        context,
      ).textTheme.headlineMedium?.copyWith(fontSize: width * 0.05),
    );
  }

  ButtonStyle buildLoginButtonStyle(
    double width,
    double height,
    Color borderColor,
  ) {
    return ElevatedButton.styleFrom(
      minimumSize: Size(width * 0.85, height * 0.06), // 최소 크기도 38.4로 설정
      maximumSize: Size(width * 0.85, height * 0.06),
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
