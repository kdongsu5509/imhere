import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:kakao_map_sdk/kakao_map_sdk.dart';

class GeofenceEnrollView extends StatelessWidget {
  const GeofenceEnrollView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        // 수평/수직 패딩 적용
        padding: EdgeInsets.symmetric(horizontal: 20.w, vertical: 10.h),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildPageTitle(context),
            // 1. 지오펜스 이름 입력
            _buildSectionTitle(context, '지오펜스 이름'),
            SizedBox(height: 8.h),
            _buildTextField(context, '회사, 학교, 집 등', Icons.label_outline),

            SizedBox(height: 32.h), // 큰 간격
            // 2. 위치 설정 (Map Placeholder)
            _buildSectionTitle(context, '위치 및 반경 설정'),
            SizedBox(height: 8.h),
            _buildMapPlaceholder(context), // 지도 영역

            SizedBox(height: 16.h),
            // 반경 설정
            _buildTextField(
              context,
              '반경 (m) 예: 100',
              Icons.social_distance_outlined,
              keyboardType: TextInputType.number,
            ),

            SizedBox(height: 32.h), // 큰 간격
            // 3. 알림 메시지 설정
            _buildSectionTitle(context, '알림 설정 및 메시지'),
            SizedBox(height: 8.h),
            _buildRecipientsSelection(context), // 수신자 선택 버튼
            SizedBox(height: 16.h),
            _buildTextField(
              context,
              '알림 메시지 예: 회사에 도착했습니다!',
              Icons.message_outlined,
              maxLines: 3,
            ),

            SizedBox(height: 48.h), // 등록 버튼 위 여백
            // 4. 저장 버튼
            _buildSaveButton(context),
          ],
        ),
      ),
    );
  }

  // ********** 빌더 함수 **********

  Widget _buildPageTitle(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          '지오펜스 등록',
          style: TextStyle(
            fontSize: 20.sp, // 반응형 폰트 크기
            fontWeight: FontWeight.bold,
          ),
        ),
        Icon(Icons.arrow_back),
      ],
    );
  }

  Widget _buildSectionTitle(BuildContext context, String title) {
    return Text(
      title,
      style: Theme.of(context).textTheme.titleMedium?.copyWith(
        fontSize: 18.sp,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget _buildMapPlaceholder(BuildContext context) {
    late KakaoMapController mapController;

    void addPoi(double lat, double lng) {
      mapController.labelLayer.addPoi(
        LatLng(lat, lng),
        style: PoiStyle(
          icon: KImage.fromAsset("/assets/images/kakaotalk_ballon", 50, 50),
        ),
      );
    }

    void onMapClick(KPoint point, LatLng position) {
      print("---------------------------------------------");
      print("카카오 지도 클릭");
      print("latitude : ${position.latitude}");
      print("longitude : ${position.longitude}");
      print("---------------------------------------------");
      addPoi(position.latitude, position.longitude);
    }

    return Container(
      height: 200.h,
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(color: Colors.grey.shade400, width: 1.w),
      ),
      child: Center(
        child: KakaoMap(
          option: const KakaoMapOption(
            position: LatLng(37.5758772, 126.9768121),
            zoomLevel: 16,
            mapType: MapType.normal,
          ),
          onMapClick: onMapClick,
          onMapReady: (KakaoMapController controller) {
            mapController = controller;
            print("카카오 지도가 정상적으로 불러와졌습니다.");
          },
          onMapError: (Error e) {
            print("카카오 지도에서 에러가 발생했습니다 : $e");
          },
        ),
      ),
    );
  }

  Widget _buildTextField(
    BuildContext context,
    String hint,
    IconData icon, {
    TextInputType keyboardType = TextInputType.text,
    int maxLines = 1,
  }) {
    return TextField(
      keyboardType: keyboardType,
      maxLines: maxLines,
      decoration: InputDecoration(
        hintText: hint,
        prefixIcon: Icon(icon, size: 22.sp),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10.r),
          borderSide: BorderSide(color: Colors.grey.shade400, width: 1.w),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10.r),
          borderSide: BorderSide(color: Colors.grey.shade400, width: 1.w),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10.r),
          borderSide: BorderSide(
            color: Theme.of(context).primaryColor,
            width: 2.w,
          ),
        ),
        contentPadding: EdgeInsets.symmetric(vertical: 16.h, horizontal: 10.w),
        hintStyle: TextStyle(fontSize: 16.sp),
      ),
    );
  }

  Widget _buildRecipientsSelection(BuildContext context) {
    // 수신자 선택 화면으로 이동하는 버튼 역할
    return OutlinedButton.icon(
      onPressed: () {
        // TODO: 수신자 선택 화면으로 이동 (예: context.push('/contact/select'))
      },
      icon: Icon(Icons.people_alt_outlined, size: 22.sp),
      label: Text('수신자 선택 (필수)', style: TextStyle(fontSize: 16.sp)),
      style: OutlinedButton.styleFrom(
        minimumSize: Size(double.infinity, 50.h), // 너비를 최대로, 높이 반응형
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.r),
        ),
        side: BorderSide(color: Colors.grey.shade400, width: 1.w),
        padding: EdgeInsets.symmetric(vertical: 8.h),
      ),
    );
  }

  Widget _buildSaveButton(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      height: 50.h,
      child: ElevatedButton(
        onPressed: () {
          // TODO: 등록 로직 구현 후 이전 화면으로 돌아가기 (예: context.pop())
          print('지오펜스 등록 완료');
        },
        style: ElevatedButton.styleFrom(
          backgroundColor: Theme.of(context).primaryColor,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10.r),
          ),
          elevation: 3,
        ),
        child: Text(
          '지오펜스 등록',
          style: TextStyle(
            fontSize: 18.sp,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        ),
      ),
    );
  }
}
