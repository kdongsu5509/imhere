import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:go_router/go_router.dart';
import 'package:iamhere/common/router/go_router.dart';

class DefaultView extends StatelessWidget {
  final Widget child;

  final String _appTitle = 'Imhere';

  static final List<String> tabs = ['/geofence', '/contact', '/record'];

  const DefaultView({super.key, required this.child});

  int _calculateSelectedIndex(BuildContext context) {
    final location = GoRouter.of(context).state.uri.toString();
    return tabs.indexWhere((path) => location.startsWith(path));
  }

  void _onItemTapped(BuildContext context, int index) {
    if (index >= 0 && index < tabs.length) {
      context.go(tabs[index]); // go_router를 사용하여 상태(URL) 변경
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final selectedIndex = _calculateSelectedIndex(context);

    return Scaffold(
      appBar: _buildAppBar(context, theme),
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
          child: Flexible(child: _buildImHereAsTitle(context, theme)),
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

  // ********** Body 및 Navigation Bar **********

  Padding _buildBodyWithPadding(BuildContext context, Widget bodyWidget) {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 15.w),
      child: Column(
        children: [
          const Divider(height: 1, thickness: 0.5, color: Colors.grey),
          Expanded(
            child: bodyWidget,
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
      onTap: (index) => onTap(context, index),
    );
  }
}
