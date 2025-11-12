import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/page_title.dart';
import 'package:iamhere/record/view/component/record_tile.dart';

class RecordView extends StatelessWidget {
  const RecordView({super.key});

  // 임시 데이터 모델 정의 (실제로는 Spring API에서 받아올 데이터 형태)
  final List<Map<String, dynamic>> _tempData = const [
    {
      'location': "회사",
      'time': 0, // 인덱스를 키로 사용
      'message': "회사에 도착했습니다!",
      'target': "팀장님",
      'device': "내 기기에서",
    },
    {
      'location': "우리집",
      'time': 1,
      'message': "퇴근 후 집에 도착!",
      'target': "가족",
      'device': "다른 기기에서",
    },
    {
      'location': "카페",
      'time': 2,
      'message': "회의 장소에 도착했습니다.",
      'target': "거래처",
      'device': "내 기기에서",
    },
  ];

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내가 보낸 메시지";
    final pageDescription = "자동으로 전송된 기록";
    final pageInfoCount = "${_tempData.length}개 전송됨"; // 리스트 길이에 맞춰 변경

    return Column(
      children: [
        // 1. 페이지 타이틀 (flex: 1)
        PageTitle(
          key: ValueKey(pageTitle),
          pageTitle: pageTitle,
          pageDescription: pageDescription,
          pageInfoCount: pageInfoCount,
          expandedWidgetFlex: 1,
        ),

        // 2. 기록 리스트 (ListView.builder 사용, flex: 4)
        Expanded(
          flex: 4,
          child: ListView.builder(
            // 리스트의 각 항목 사이에 간격을 주기 위해 Padding 적용
            padding: EdgeInsets.zero, // 기본 패딩 제거 (필요하다면 추가 가능)
            itemCount: _tempData.length,
            itemBuilder: (context, index) {
              final item = _tempData[index];

              // 실제 시간을 현재 시간에서 인덱스를 빼서 다르게 보이도록 설정
              final recordTime = DateTime.now().subtract(
                Duration(days: index, minutes: item['time'] * 10),
              );

              return RecordTile(
                // ValueKey를 사용하여 각 아이템에 고유한 키를 부여합니다.
                tileKey: ValueKey('record_tile_${item['time']}'),

                locationName: item['location'] as String,
                recordTime: recordTime,
                message: item['message'] as String,
                targetName: item['target'] as String,
                deviceLocation: item['device'] as String,
              );
            },
          ),
        ),
      ],
    );
  }
}
