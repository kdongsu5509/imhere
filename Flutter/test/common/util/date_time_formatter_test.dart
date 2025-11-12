import 'package:flutter_test/flutter_test.dart';
import 'package:iamhere/common/util/date_time_formatter.dart';

void main() {
  group("시간 형태 변환 테스트", () {
    test("success_정상_포맷팅_테스트 (두 자릿수 포함)", () {
      // given: 모든 요소가 두 자릿수인 정상적인 날짜
      DateTime testDate = DateTime(2025, 11, 12, 15, 30);
      String expectedOutput = "2025-11-12 15:30";

      // when
      String result = formatDateTime(testDate);

      // then
      expect(result, expectedOutput);
    });

    test("success_패딩_적용_테스트 (한 자릿수 포함)", () {
      // given: 월, 일, 시간, 분이 모두 한 자릿수인 경우
      DateTime testDate = DateTime(2024, 3, 5, 8, 7);
      String expectedOutput = "2024-03-05 08:07";

      // when
      String result = formatDateTime(testDate);

      // then
      expect(result, expectedOutput);
    });

    test("failure_case_일부_패딩_적용", () {
      // given: 월, 일은 두 자릿수, 시간, 분은 한 자릿수인 경우
      DateTime testDate = DateTime(2023, 10, 20, 9, 1);
      String expectedOutput = "2023-10-20 09:01";

      // when
      String result = formatDateTime(testDate);

      // then
      expect(result, expectedOutput);
    });
  });
}
