import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/common/router/go_router.dart';
import 'package:iamhere/common/theme/im_here_them_data_light.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';

Future main() async {
  await dotenv.load(fileName: "iam_here_flutter_secret.env");
  WidgetsFlutterBinding.ensureInitialized();
  KakaoSdk.init(nativeAppKey: dotenv.env['KAKAO_NATIVE_APP_KEY']);
  await FlutterNaverMap().init(
    clientId: dotenv.env['NAVER_CLIENT_ID'],
    onAuthFailed: (ex) {
      switch (ex) {
        case NQuotaExceededException(:final message):
          debugPrint("사용량 초과 (message: $message)");
          break;
        case NUnauthorizedClientException() ||
            NClientUnspecifiedException() ||
            NAnotherAuthFailedException():
          debugPrint("인증 실패: $ex");
          break;
      }
    },
  );
  runApp(const ProviderScope(child: ImHereApp()));
}

class ImHereApp extends StatefulWidget {
  const ImHereApp({super.key});

  @override
  State<ImHereApp> createState() => _ImHereAppState();
}

class _ImHereAppState extends State<ImHereApp> {
  @override
  Widget build(BuildContext context) {
    return ScreenUtilInit(
      designSize: const Size(402, 874),
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (context, child) {
        return MaterialApp.router(
          debugShowCheckedModeBanner: false,
          title: 'ImHere App Dev',
          theme: lightTheme,
          routerConfig: router,
        );
      },
    );
  }
}
