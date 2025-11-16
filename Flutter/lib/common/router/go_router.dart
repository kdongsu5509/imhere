import 'package:flutter/material.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';
import 'package:go_router/go_router.dart';
import 'package:iamhere/auth/view/auth_view.dart';
import 'package:iamhere/common/view_component/default_view.dart';
import 'package:iamhere/contact/view/contact_view.dart';
import 'package:iamhere/geofence/view/component/map_select_view.dart';
import 'package:iamhere/geofence/view/geofence_enroll_view.dart';
import 'package:iamhere/geofence/view/geofence_view.dart';
import 'package:iamhere/record/view/record_view.dart';

final GoRouter router = GoRouter(
  initialLocation: '/geofence',

  // 모든 라우트 정의
  routes: [
    ShellRoute(
      builder: (context, state, child) {
        return DefaultView(child: child);
      },

      // builder: (context, state, child) {
      //   return AuthView();
      // },
      routes: [
        GoRoute(
          path: '/geofence',
          pageBuilder: (context, state) => buildPageWithoutTransition(
            context: context,
            state: state,
            child: const GeofenceView(),
          ),
          routes: [
            GoRoute(
              path: '/enroll',
              pageBuilder: (context, state) => buildPageWithBottomUpTransition(
                context: context,
                state: state,
                child: const GeofenceEnrollView(),
              ),
            ),
          ],
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

        GoRoute(
          path: '/register',
          builder: (context, state) => const Text("새 등록 페이지"),
        ),
      ],
    ),
  ],

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
    transitionDuration: Duration.zero,
    reverseTransitionDuration: Duration.zero,

    transitionsBuilder: (context, animation, secondaryAnimation, child) {
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
    transitionDuration: const Duration(milliseconds: 300),

    transitionsBuilder: (context, animation, secondaryAnimation, child) {
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
