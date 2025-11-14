import 'package:json_annotation/json_annotation.dart';

part 'contact.g.dart';

@JsonSerializable()
class Contact {
  final String name;
  final String number;

  Contact({required this.name, required this.number});

  // 4. JSON 직렬화 팩토리 메서드 추가 (Map -> Contact)
  factory Contact.fromJson(Map<String, dynamic> json) =>
      _$ContactFromJson(json);

  // 5. JSON 역직렬화 메서드 추가 (Contact -> Map)
  Map<String, dynamic> toJson() => _$ContactToJson(this);
}
