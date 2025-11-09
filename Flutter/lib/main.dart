import 'package:flutter/material.dart';
import 'package:iamhere/common/theme/im_here_them_data_dark.dart';
import 'package:iamhere/common/theme/im_here_them_data_light.dart';

void main() {
  runApp(const ImHereApp());
}

class ImHereApp extends StatefulWidget {
  const ImHereApp({super.key});

  @override
  State<ImHereApp> createState() => _ImHereAppState();
}

class _ImHereAppState extends State<ImHereApp> {
  ThemeMode _themeMode = ThemeMode.light;

  void toggleTheme(bool isDark) {
    setState(() {
      _themeMode = isDark ? ThemeMode.dark : ThemeMode.light;
    });
  }
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ImHere App Dev',
      theme: lightTheme,
      darkTheme: darkTheme,
      themeMode: _themeMode,
      home: HomeScreen(toggleTheme: toggleTheme, isDarkMode: _themeMode == ThemeMode.dark),
    );
  }
}
class HomeScreen extends StatelessWidget {
  final Function(bool) toggleTheme;
  final bool isDarkMode;

  const HomeScreen({super.key, required this.toggleTheme, required this.isDarkMode});

  @override
  Widget build(BuildContext context) {
    // Theme.of(context)를 사용하여 현재 적용된 테마의 속성을 가져옵니다.
    final theme = Theme.of(context);

    return Scaffold(
      // AppBar는 theme: lightTheme 또는 darkTheme에 정의된 스타일을 자동으로 따릅니다.
      appBar: AppBar(
        title: const Text('위치 알람 (Imhere)'),
        actions: [
          // 테마 전환 버튼
          Switch(
            value: isDarkMode,
            onChanged: toggleTheme,
            activeColor: theme.colorScheme.secondary,
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              '현재 테마: ${isDarkMode ? '다크 모드' : '라이트 모드'}',
              // TextStyle에 테마의 텍스트 색상 등을 사용할 수 있습니다.
              style: TextStyle(color: theme.textTheme.bodyLarge?.color),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                // 버튼 스타일도 ThemeData를 따릅니다.
              },
              child: const Text('테마가 적용된 버튼'),
            ),
          ],
        ),
      ),
      // BottomNavigationBar도 테마 설정을 따릅니다.
      bottomNavigationBar: BottomNavigationBar(
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.location_on), label: '지오펜스'),
          BottomNavigationBarItem(icon: Icon(Icons.people), label: '연락처'),
          BottomNavigationBarItem(icon: Icon(Icons.history), label: '기록'),
        ],
      ),
    );
  }
}