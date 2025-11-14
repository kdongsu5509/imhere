import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart'; // ✅ ScreenUtil import
import 'package:iamhere/common/util/phone_number_formatter.dart';
// import 'package:iamhere/common/view_component/FlexibleScreen.dart'; // ❌ FlexibleScreen 제거

class ContactTile extends StatelessWidget {
  final String contactName;
  final String phoneNumber;

  const ContactTile({
    super.key,
    required this.contactName,
    required this.phoneNumber,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.all(
          Radius.circular(20.r),
        ), // 20px radius를 반응형으로 (.r)
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withValues(alpha: 0.1),
            spreadRadius: 1.r,
            blurRadius: 5.r,
            offset: Offset(0, 3.h), // 높이 기준으로 그림자 오프셋 설정
          ),
        ],
      ),
      width: 1.sw, // 화면 너비 100% (1.sw)
      height: 80.h, // 높이 80px을 화면 높이 기준으로 반응형 설정
      padding: EdgeInsets.symmetric(horizontal: 16.w), // 너비 기준으로 패딩 설정
      margin: EdgeInsets.symmetric(vertical: 8.h), // 높이 기준으로 마진 설정

      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // 1. 정보 영역
          Expanded(
            flex: 4,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // 연락처 이름
                Text(
                  contactName,
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                    fontSize: 20.sp, // 폰트 크기 (sp 사용)
                    color: Colors.black,
                  ),
                ),
                SizedBox(height: 4.h), // 높이 기준으로 간격 설정
                // 전화번호 행
                Row(
                  children: [
                    Icon(Icons.call, size: 16.sp, color: Colors.grey), // 아이콘 크기
                    SizedBox(width: 4.w), // 너비 기준으로 간격 설정
                    Text(
                      convertToPhoneNumber(phoneNumber),
                      style: TextStyle(
                        color: Colors.grey,
                        fontSize: 15.sp,
                      ), // 폰트 크기
                    ),
                  ],
                ),
              ],
            ),
          ),

          // 2. 삭제 버튼 영역
          IconButton(
            // Expanded가 필요 없으므로 IconButton만 남김
            icon: Icon(
              Icons.delete_forever_outlined,
              color: Colors.red,
              size: 24.w, // 너비 기준으로 아이콘 크기 설정
            ),
            onPressed: () {
              // TODO: 삭제 로직 호출
            },
          ),
        ],
      ),
    );
  }
}
