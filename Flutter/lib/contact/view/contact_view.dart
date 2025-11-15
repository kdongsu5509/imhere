import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart'; // ✅ ScreenUtil import
// import 'package:iamhere/common/view_component/FlexibleScreen.dart'; // ❌ FlexibleScreen 제거
import 'package:iamhere/common/view_component/page_title.dart';
import 'package:iamhere/contact/view/component/contact_tile.dart';
import 'package:iamhere/contact/view_model/contact_view_model_provider.dart';

class ContactView extends ConsumerStatefulWidget {
  const ContactView({super.key});

  @override
  ConsumerState<ContactView> createState() => _ContactViewState();
}

class _ContactViewState extends ConsumerState<ContactView> {
  final List<Map<String, String>> _contactList = const [
    {"name": "고동수", "phone": "01073512781"},
    {"name": "프로젝트 개발자A", "phone": "01011112222"},
    {"name": "고객지원팀", "phone": "01098765432"},
    {"name": "테스트 계정", "phone": "01000000000"},
    {"name": "김철수", "phone": "01012345678"},
    {"name": "이영희", "phone": "01055554444"},
  ];

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내 친구 목록";
    final pageDescription = "메시지를 받을 친구들";
    final pageInfoCount = "${_contactList.length}명 등록됨";

    return Column(
      children: [
        // 1. 페이지 타이틀 (추가 위젯 포함)
        PageTitle(
          key: ValueKey(pageTitle),
          pageTitle: pageTitle,
          pageDescription: pageDescription,
          pageInfoCount: pageInfoCount,
          additionalWidget: _buildContactImportButton(context),
          interval:
              2, // 이 interval 값은 FlexibleScreen을 가정하고 있으므로, PageTitle 내부도 ScreenUtil로 수정해야 합니다.
        ),

        // 2. 연락처 목록 (ListView.builder 적용)
        Expanded(
          flex: 5,
          child: ListView.builder(
            padding: EdgeInsets.zero, // Expanded 내부에서는 기본 패딩 제거
            itemCount: _contactList.length,
            itemBuilder: (context, index) {
              final contact = _contactList[index];
              return ContactTile(
                key: ValueKey(contact['phone']), // 키를 전화번호로 지정
                contactName: contact['name']!,
                phoneNumber: contact['phone']!,
              );
            },
          ),
        ),
      ],
    );
  }

  // 폰에서 친구 연락처 불러오기 버튼을 ScreenUtil 기반으로 재구성
  Widget _buildContactImportButton(BuildContext context) {
    final vm = ref.read(contactViewModelInterfaceProvider);
    return Container(
      // 높이를 60px 기준으로 반응형 설정
      height: 60.h,
      decoration: BoxDecoration(
        color: Theme.of(context).primaryColor,
        // radius를 50px 기준으로 반응형 설정 (.w 또는 .r 사용)
        borderRadius: BorderRadius.all(Radius.circular(50.r)),
      ),
      child: Center(
        child: TextButton(
          // TextButton으로 변경하여 클릭 액션 추가
          onPressed: () {
            vm.selectContact();
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('폰 연락처 불러오기 기능 실행 via Flutter Method Channel'),
              ),
            );
          },
          child: Text(
            "폰에서 친구 연락처 불러오기",
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(
              color: Theme.of(context).colorScheme.surface,
              fontWeight: FontWeight.bold,
              fontSize: 18.sp,
            ),
          ),
        ),
      ),
    );
  }
}
