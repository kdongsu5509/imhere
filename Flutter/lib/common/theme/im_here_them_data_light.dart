import 'package:flutter/material.dart';

final ThemeData lightTheme = ThemeData(
  brightness: Brightness.light,
  scaffoldBackgroundColor: Colors.white,

  splashColor: Colors.transparent,
  highlightColor: Colors.transparent,

  // **2. 색상 구성표 (ColorScheme)**
  colorScheme:
      ColorScheme.fromSwatch(
        primarySwatch: MaterialColor(
          0xFF48D1CC, // 에메랄드 청록색
          <int, Color>{
            50: Color(0xFFE0F7F7),
            100: Color(0xFFB3ECEC),
            200: Color(0xFF80DFDF),
            300: Color(0xFF4DD2D2),
            400: Color(0xFF26CACA),
            500: Color(0xFF00C2C2),
            600: Color(0xFF00BABA),
            700: Color(0xFF00B0B0),
            800: Color(0xFF00A6A6),
            900: Color(0xFF008F8F),
          },
        ),
        brightness: Brightness.light,
      ).copyWith(
        // 💡 Primary: 앱의 주요 상호 작용 요소에 사용되는 색상 (300 쉐이드 사용)
        primary: const Color(0xFF48D1CC),

        // 💡 Secondary (강조 색상): 텍스트에 대비되는 어두운 색상을 사용합니다.
        secondary: Colors.black,

        // 💡 Surface (카드/배경 위젯 색상): 흰색 유지
        surface: Colors.white,

        // tertiary: Color(0xFF008F8F), -> 다크 모드
        tertiary: Color(0xFFD9F7F7),
      ),

  // **3. AppBar 스타일**
  appBarTheme: const AppBarTheme(
    backgroundColor: Colors.white,
    foregroundColor: Colors.black, // 제목 및 아이콘 색상
    elevation: 1, // 카드나 앱바에 약한 그림자를 줍니다.
  ),

  // **4. Bottom Navigation Bar 스타일**
  bottomNavigationBarTheme: const BottomNavigationBarThemeData(
    backgroundColor: Colors.white,
    selectedItemColor: Color(0xFF48D1CC),
    unselectedItemColor: Colors.grey,
  ),

  // **5. 텍스트 테마 (선택 사항)**]
  fontFamily: 'BMHANNAAir',
  textTheme: const TextTheme(
    headlineLarge: TextStyle(fontFamily: 'BMDOHYEON', color: Color(0xFF48D1CC)),

    headlineMedium: TextStyle(fontFamily: 'BMJUA', fontWeight: FontWeight.w400),

    bodyLarge: TextStyle(
      fontFamily: 'BMHANNAAir',
      fontSize: 16,
      fontWeight: FontWeight.w400,
    ),
    bodyMedium: TextStyle(
      fontFamily: 'BMHANNAAir',
      fontSize: 14,
      fontWeight: FontWeight.w400,
    ),
    labelLarge: TextStyle(
      fontFamily: 'BMHANNAAir',
      fontSize: 14,
      fontWeight: FontWeight.w500,
    ),
  ),
);
