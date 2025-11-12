import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import 'package:iamhere/common/router/go_router.dart'; // go_router import

// 📌 StatelessWidget으로 변경: 내부 상태(selectedIndex)를 제거하고 go_router에 의존
class DefaultView extends StatelessWidget {
  final Widget child; // ShellRoute가 전달하는 현재 화면(GeofenceView, ContactView 등)

  final String _appTitle = 'Imhere';

  // 💡 라우팅 경로 정의 (라우터 파일과 일치해야 함)
  static final List<String> tabs = ['/geofence', '/contact', '/record'];

  const DefaultView({super.key, required this.child});

  // 현재 URL 경로를 기반으로 BottomNavigationBar의 인덱스를 계산합니다.
  int _calculateSelectedIndex(BuildContext context) {
    // GoRouter.of(context).location 대신 state.uri.toString()을 쓰는 것이 더 명확할 수 있습니다.
    // 여기서는 가장 간단한 GoRouter.of(context).location을 사용합니다.
    final location = GoRouter.of(context).state.uri.toString();
    return tabs.indexWhere((path) => location.startsWith(path));
  }

  // BottomNavigationBar 탭 클릭 시 URL을 변경하여 화면을 전환합니다.
  void _onItemTapped(BuildContext context, int index) {
    if (index >= 0 && index < tabs.length) {
      context.go(tabs[index]); // go_router를 사용하여 상태(URL) 변경
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    // 현재 경로에 맞는 인덱스를 계산합니다.
    final selectedIndex = _calculateSelectedIndex(context);

    return Scaffold(
      appBar: _buildAppBar(context, theme),
      // 📌 body에 child 위젯을 전달하여 현재 경로에 맞는 화면을 표시
      body: _buildBodyWithPadding(context, child),
      bottomNavigationBar: _buildBottomNavigationBar(
        context,
        selectedIndex,
        _onItemTapped,
      ),
      floatingActionButton: FloatingActionButton(
        shape: const CircleBorder(),
        child: Icon(
          Icons.add_rounded,
          color: theme.colorScheme.surface,
          size: 28.sp, // 반응형 크기 적용
        ),
        onPressed: () {
          router.go("/geofence/enroll");
        },
      ),
    );
  }

  // ********** AppBar 관련 위젯 **********

  PreferredSize _buildAppBar(BuildContext context, ThemeData theme) {
    return PreferredSize(
      preferredSize: const Size.fromHeight(kToolbarHeight),
      child: AppBar(
        centerTitle: false,
        backgroundColor: Colors.transparent,
        elevation: 0.0,
        scrolledUnderElevation: 0,
        automaticallyImplyLeading: false,
        titleSpacing: 0,
        title: Padding(
          padding: EdgeInsets.symmetric(horizontal: 15.w), // 15px 가로 패딩
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Flexible로 감싸서 텍스트 오버플로우 방지
              Flexible(child: _buildImHereAsTitle(context, theme)),
              SizedBox(width: 8.w), // 간격 확보
              _buildAppBarButton(theme),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildImHereAsTitle(BuildContext context, ThemeData theme) {
    return Text(
      _appTitle,
      style: theme.textTheme.headlineLarge?.copyWith(
        fontSize: 35.sp, // 반응형 폰트 크기
        fontWeight: FontWeight.bold,
      ),
    );
  }

  // 오버플로우 방지를 위해 mainAxisSize.min 명시
  Row _buildAppBarButton(ThemeData theme) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          onPressed: () {
            // TODO: 로그아웃 로직 (Spring Security 연동)
          },
          icon: const Icon(Icons.logout_outlined),
        ),
      ],
    );
  }

  // ********** Body 및 Navigation Bar **********

  Padding _buildBodyWithPadding(BuildContext context, Widget bodyWidget) {
    return Padding(
      // 수평 패딩 적용
      padding: EdgeInsets.symmetric(horizontal: 15.w),
      child: Column(
        children: [
          const Divider(height: 1, thickness: 0.5, color: Colors.grey),
          Expanded(
            child: bodyWidget, // ShellRoute에서 받은 위젯
          ),
        ],
      ),
    );
  }

  BottomNavigationBar _buildBottomNavigationBar(
    BuildContext context,
    int currentIndex,
    Function(BuildContext, int) onTap,
  ) {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      enableFeedback: false,
      elevation: 0,

      items: const [
        BottomNavigationBarItem(
          icon: Icon(Icons.location_on_outlined),
          label: '지오펜스',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.people_outline_outlined),
          label: '연락처',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.history), label: '기록'),
      ],
      currentIndex: currentIndex,
      // 📌 onTap 호출 시 context와 index 전달
      onTap: (index) => onTap(context, index),
    );
  }
}
