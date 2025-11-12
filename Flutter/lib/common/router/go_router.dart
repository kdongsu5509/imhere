import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:iamhere/common/view_component/default_view.dart';
import 'package:iamhere/contact/view/contact_view.dart';
import 'package:iamhere/geofence/view/geofence_view.dart';
import 'package:iamhere/record/view/record_view.dart';

// 앱 전체에서 사용할 라우터 인스턴스 정의
final GoRouter router = GoRouter(
  // 앱의 시작 경로 (가장 먼저 보여줄 화면)
  initialLocation: '/geofence',

  // 모든 라우트 정의
  routes: [
    // 💡 ShellRoute: DefaultView를 공통 레이아웃으로 사용합니다.
    ShellRoute(
      // DefaultView는 상단의 AppBar와 하단의 BottomNavigationBar를 제공하는 껍데기 역할
      builder: (context, state, child) {
        // DefaultView에 ShellRoute가 전달하는 현재 페이지 위젯(child)을 전달합니다.
        return DefaultView(child: child);
      },

      // ShellRoute 내부에 BottomNavigationBar의 각 탭에 해당하는 경로를 정의합니다.
      routes: [
        // 1. 지오펜스 탭 경로
        GoRoute(
          path: '/geofence',
          pageBuilder: (context, state) => buildPageWithoutTransition(
            context: context,
            state: state,
            child: const GeofenceView(),
          ),
        ),

        // 2. 연락처 탭 경로
        GoRoute(
          path: '/contact',
          pageBuilder: (context, state) => buildPageWithoutTransition(
            context: context,
            state: state,
            child: const ContactView(),
          ),
        ),

        // 3. 기록 탭 경로
        GoRoute(
          path: '/record',
          pageBuilder: (context, state) => buildPageWithoutTransition(
            context: context,
            state: state,
            child: const RecordView(),
          ),
        ),

        // [추가 예시] FloatingActionButton 클릭 시 나타나는 새 등록 페이지 (Shell 밖)
        GoRoute(
          path: '/register',
          // Builder에서 DefaultView를 감쌀 필요 없음 (전체 화면으로 덮기)
          builder: (context, state) => const Text("새 등록 페이지"),
        ),
      ],
    ),
  ],

  // 에러 발생 시 처리 (선택 사항)
  errorBuilder: (context, state) =>
      const Center(child: Text("페이지를 찾을 수 없습니다.")),
);

CustomTransitionPage buildPageWithoutTransition({
  required BuildContext context,
  required GoRouterState state,
  required Widget child,
}) {
  return CustomTransitionPage(
    key: state.pageKey,
    child: child,
    // 시간을 0으로 설정하여 즉시 전환되게 합니다.
    transitionDuration: Duration.zero,
    reverseTransitionDuration: Duration.zero,

    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      // 어떤 애니메이션도 적용하지 않고 child(페이지 위젯)를 바로 반환
      return child;
    },
  );
}

CustomTransitionPage buildPageWithFadeTransition({
  required BuildContext context,
  required GoRouterState state,
  required Widget child,
}) {
  return CustomTransitionPage(
    key: state.pageKey,
    child: child,
    // 전환 시간을 300ms로 설정 (부드러운 전환)
    transitionDuration: const Duration(milliseconds: 300),

    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      // FadeTransition을 사용하여 화면이 부드럽게 나타나게 합니다.
      return FadeTransition(opacity: animation, child: child);
    },
  );
}

CustomTransitionPage buildPageWithBottomUpTransition({
  required BuildContext context,
  required GoRouterState state,
  required Widget child,
}) {
  return CustomTransitionPage(
    key: state.pageKey,
    child: child,
    transitionDuration: const Duration(milliseconds: 350),

    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      // 💡 아래에서 위로 (Bottom-up) 슬라이드 애니메이션 정의
      const begin = Offset(0.0, 1.0); // 시작 지점: 화면 아래 (y=1.0)
      const end = Offset.zero; // 도착 지점: 화면 중앙 (y=0.0)

      final tween = Tween(
        begin: begin,
        end: end,
      ).chain(CurveTween(curve: Curves.easeOut)); // 부드러운 전환 커브 적용

      return SlideTransition(position: animation.drive(tween), child: child);
    },
  );
}
