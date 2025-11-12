import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/common/router/go_router.dart';
import 'package:iamhere/common/theme/im_here_them_data_light.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:kakao_map_sdk/kakao_map_sdk.dart';

Future main() async {
  await dotenv.load(fileName: "iam_here_flutter_secret.env");
  WidgetsFlutterBinding.ensureInitialized();
  KakaoSdk.init(nativeAppKey: dotenv.env['KAKAO_NATIVE_APP_KEY']);
  print("카카오 SDK : ${KakaoSdk.appKey}");
  await KakaoMapSdk.instance.initialize(
    dotenv.env['KAKAO_NATIVE_APP_KEY_DEV']!,
  );
  var s = await KakaoMapSdk.instance.hashKey();
  print("카카오 맵 HASHKEY $s");
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
      minTextAdapt: true, // 작은 화면에서 텍스트 크기 조절 허용
      splitScreenMode: true, // 멀티 윈도우 지원
      builder: (context, child) {
        return MaterialApp.router(
          title: 'ImHere App Dev',
          theme: lightTheme,
          routerConfig: router,
        );
      },
    );
  }
}
