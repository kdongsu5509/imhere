import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

// enum은 그대로 사용
enum Device {
  MY("내 기기에서"),
  SERVER("서버에서");

  final String value;

  const Device(this.value);
}

class DeviceTile extends StatelessWidget {
  final Device device;

  const DeviceTile._({super.key, required this.device});

  factory DeviceTile.my({Key? key}) {
    return DeviceTile._(key: key, device: Device.MY);
  }

  factory DeviceTile.server({Key? key}) {
    return DeviceTile._(key: key, device: Device.SERVER);
  }

  @override
  Widget build(BuildContext context) {
    final String description = device.value;

    return Container(
      padding: EdgeInsets.symmetric(horizontal: 10.w, vertical: 4.h),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.all(Radius.circular(5.r)),
      ),
      child: Center(
        child: Text(
          description,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            fontSize: 15.sp, // 폰트 크기
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}
