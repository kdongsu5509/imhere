import 'package:flutter/material.dart';

final ThemeData darkTheme = ThemeData(
  // **1. 기본 설정 (Dark)**
  brightness: Brightness.dark,

  // 기본 배경색: Material 3의 일반적인 다크 모드 배경색
  // React의 '--dark-background'에 상응합니다.
  scaffoldBackgroundColor: const Color(0xFF121212),

  // **2. 색상 구성표 (ColorScheme)**
  // ColorScheme.fromSwatch()를 사용하여 기존 MaterialColor 팔레트를 재사용하되,
  // 다크 모드에 맞게 밝기와 대비를 조정합니다.
  colorScheme: ColorScheme.fromSwatch(
    // 라이트 모드에서 정의한 동일한 MaterialColor 팔레트를 사용
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
    brightness: Brightness.dark, // 다크 모드 밝기 적용
  ).copyWith(
    // 💡 PrimaryColor: 다크 모드에서는 대비를 위해 400~500 쉐이드가 잘 사용됩니다.
    // 기존 primaryColor(0xFF48D1CC)는 300 쉐이드에 가깝습니다.
    primary: const Color(0xFF4DD2D2), // 라이트 모드보다 살짝 밝거나 유지

    // 💡 Secondary (강조 색상): 라이트 모드의 'black' 대신,
    // 다크 모드 배경과 대비되도록 밝은 흰색이나 밝은 회색을 사용합니다.
    secondary: Colors.white70,

    // 💡 Surface (카드/배경 위젯 색상): 배경보다 살짝 밝게 설정하여 경계 구분
    surface: const Color(0xFF1E1E1E),
  ),

  // 앱 주요 색상: ColorScheme의 primary를 따르도록 MaterialColor 대신 사용합니다.
  primaryColor: const Color(0xFF4DD2D2),


  // **3. AppBar 스타일**
  appBarTheme: const AppBarTheme(
    backgroundColor: Color(0xFF1E1E1E), // Surface 색상과 유사하게 설정
    foregroundColor: Colors.white,      // 제목 및 아이콘 색상을 흰색으로
    elevation: 0,                       // 다크 모드에서는 그림자를 줄이는 경우가 많습니다.
  ),

  // **4. Bottom Navigation Bar 스타일**
  bottomNavigationBarTheme: const BottomNavigationBarThemeData(
    backgroundColor: Color(0xFF1E1E1E),     // AppBar와 일관성 유지
    selectedItemColor: Color(0xFF4DD2D2),   // primary 색상을 사용하여 강조
    unselectedItemColor: Colors.grey,       // 비활성 색상은 유지
    selectedIconTheme: IconThemeData(size: 28), // 선택된 아이콘을 살짝 키워서 강조
  ),

  // **5. 텍스트 테마 (선택 사항)**
  fontFamily: 'BMHANNAAir',
  textTheme: const TextTheme(
    headlineLarge: TextStyle(fontFamily: 'BMDOHYEON'),

    headlineMedium: TextStyle(fontFamily: 'BMJUA', fontWeight: FontWeight.w400),

    bodyLarge: TextStyle(fontFamily: 'BMHANNAAir', fontSize: 16, fontWeight: FontWeight.w400),
    bodyMedium: TextStyle(fontFamily: 'BMHANNAAir', fontSize: 14, fontWeight: FontWeight.w400),
    labelLarge: TextStyle(fontFamily: 'BMHANNAAir', fontSize: 14, fontWeight: FontWeight.w500),
  ),
);