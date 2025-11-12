import 'package:flutter_test/flutter_test.dart';
import 'package:iamhere/common/util/phone_number_formatter.dart';

void main() {
  group("전화번호 포맷팅 테스트", () {
    test("success_format", () {
      //given
      String input = "01012345678";
      String output = "010-1234-5678";
      //when
      String result = convertToPhoneNumber(input);
      //then
      expect(result, output);
    });

    test("check_fail_case", () {
      //given
      final inputs = ["0101234", "010 123 123", "01012+12"];
      final expectation = ["0101234", "010123123", "0101212"];
      //when, then
      for (int i = 0; i < 3; i++) {
        expect(convertToPhoneNumber(inputs[i]), expectation[i]);
      }
    });
  });
}
