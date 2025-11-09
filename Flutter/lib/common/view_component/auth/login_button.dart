import 'package:flutter/material.dart';

Widget kakaoLoginButton({required BuildContext context, required VoidCallback onPressed}) {
  final width = MediaQuery.of(context).size.width;
  final height = MediaQuery.of(context).size.height;

  return ClipRRect(
      borderRadius: BorderRadius.circular(35.0),
    child: Container(
      width: width * 0.85,
      height: height * 0.06,
      color: const Color(0xFFFEE500),
        child: Center(child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset("assets/images/talk_ballon2.png" , width: height * 0.02, height: height * 0.02,),
            SizedBox(width: width * 0.04,),
            Text("Kakao 로그인", style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                fontSize: width * 0.05
            ),),
            SizedBox(width: width * 0.02,),
          ],
        ))
    ),
  );
}

Widget googleLoginButton({required BuildContext context, required VoidCallback onPressed}) {
  final width = MediaQuery.of(context).size.width;
  final height = MediaQuery.of(context).size.height;

  return ClipRRect(
    borderRadius: BorderRadius.circular(35.0),
    child: Container(
        width: width * 0.85,
        height: height * 0.06,
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(color: Colors.grey),
          borderRadius: BorderRadius.circular(35.0),
    ),
      child: Center(child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset("assets/images/google_logo.png" , width: height * 0.02, height: height * 0.02,),
          SizedBox(width: width * 0.04,),
          Text("Google 로그인", style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              fontSize: width * 0.05
          ),),
          SizedBox(width: width * 0.02,),
        ],
      )),
    ),
  );
}