import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart'; // ✅ ScreenUtil import
// import 'package:iamhere/common/view_component/FlexibleScreen.dart'; // ❌ FlexibleScreen 제거
import 'package:iamhere/common/view_component/page_title.dart';
import 'package:iamhere/geofence/view/component/geofence_tile.dart';

class GeofenceView extends StatefulWidget {
  const GeofenceView({super.key});

  @override
  State<GeofenceView> createState() => _GeofenceViewState();
}

class _GeofenceViewState extends State<GeofenceView>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  // 임시 지오펜스 목록 데이터 (실제로는 API에서 불러옴)
  final List<Map<String, dynamic>> _geofenceList = const [
    {"name": "우리집", "address": "서울시 강남구", "members": 2},
    {"name": "회사", "address": "서울시 서초구", "members": 1},
  ];

  // 상태 관리 (첫 번째 타일만 예시로 사용)
  bool _isHomeActive = true;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _handleToggle(bool newValue) {
    setState(() {
      _isHomeActive = newValue;
    });
    // TODO: 여기에 Spring Boot API 호출 로직 추가 (상태 저장)
  }

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내 위치 기반 알림";
    final pageDescription = "특정 위치에 도착하면 친구에게 자동으로 메시지를 보냅니다";
    final pageInfoCount = "${_geofenceList.length}개 등록됨";

    return Column(
      children: [
        // 1. 페이지 타이틀 (추가 위젯 포함)
        PageTitle(
          key: ValueKey(pageTitle),
          pageTitle: pageTitle,
          pageDescription: pageDescription,
          pageInfoCount: pageInfoCount,
          // 💡 _buildGPSInfoTrackingUsingDescription 함수로 이름 변경
          additionalWidget: _buildGPSInfoTrackingUsingDescription(),
          interval: 2,
        ),

        // 2. 지오펜스 타일 목록
        Expanded(
          flex: 5,
          child: ListView.builder(
            padding: EdgeInsets.zero,
            itemCount: _geofenceList.length,
            itemBuilder: (context, index) {
              final data = _geofenceList[index];

              // 첫 번째 타일만 _isHomeActive 상태를 사용하도록 설정
              final isToggled = (index == 0) ? _isHomeActive : !_isHomeActive;

              return GeofenceTile(
                homeName: data['name'] as String,
                address: data['address'] as String,
                memberCount: data['members'] as int,
                isToggleOn: isToggled,
                onToggleChanged: _handleToggle,
              );
            },
          ),
        ),
      ],
    );
  }

  // GPS 추적 정보 표시 위젯 (ScreenUtil 적용)
  Widget _buildGPSInfoTrackingUsingDescription() {
    return Container(
      // 높이를 40px 기준으로 반응형 설정
      height: 40.h,
      decoration: BoxDecoration(
        color: Theme.of(context).primaryColor,
        borderRadius: BorderRadius.all(
          // radius를 20px 기준으로 반응형 설정
          Radius.circular(20.r),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [_buildBlinkingGPSIcon(), _buildDescription()],
      ),
    );
  }

  // 위치 추적 설명 텍스트 (ScreenUtil 적용)
  Widget _buildDescription() {
    final descriptionMessage = "위치 추적 중이에요";
    return Text(
      descriptionMessage,
      style: Theme.of(context).textTheme.headlineMedium?.copyWith(
        color: Theme.of(context).colorScheme.surface,
        fontWeight: FontWeight.bold,
        fontSize: 16.sp,
      ),
    );
  }

  // 깜빡이는 GPS 아이콘 (ScreenUtil 적용)
  FadeTransition _buildBlinkingGPSIcon() {
    return FadeTransition(
      opacity: _controller,
      child: Padding(
        padding: EdgeInsets.fromLTRB(
          20.w, // 좌 패딩
          0,
          4.w, // 우 패딩
          0,
        ),
        child: Icon(
          Icons.location_on_outlined,
          color: Colors.red,
          size: 25.sp, // 아이콘 크기를 20sp로 설정
        ),
      ),
    );
  }
}
