import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:iamhere/contact/view_model/contact.dart';

class RecipientTile extends StatelessWidget {
  final Contact contact;
  final bool isSelected;
  final VoidCallback onTap;

  const RecipientTile({
    required this.contact,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 12.h),
        decoration: _recipientDecoration(),
        child: Row(
          children: [
            // 체크박스
            Checkbox(value: isSelected, onChanged: (_) => onTap()),
            SizedBox(width: 12.w),
            // 아이콘
            _buildCircleAvatar(),
            SizedBox(width: 16.w),
            // 이름과 전화번호
            buildNameAndNumber(),
            // 선택 표시
            if (isSelected)
              Icon(
                Icons.check_circle,
                color: Theme.of(context).primaryColor,
                size: 24.sp,
              ),
          ],
        ),
      ),
    );
  }

  CircleAvatar _buildCircleAvatar() {
    return CircleAvatar(
      radius: 24.r,
      backgroundColor: isSelected ? Colors.blue[200] : Colors.grey[300],
      child: Text(
        contact.name.isNotEmpty ? contact.name[0] : '?',
        style: TextStyle(
          fontSize: 18.sp,
          fontWeight: FontWeight.bold,
          color: isSelected ? Colors.blue[700] : Colors.grey[700],
        ),
      ),
    );
  }

  Expanded buildNameAndNumber() {
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            contact.name,
            style: TextStyle(
              fontSize: 16.sp,
              fontWeight: FontWeight.bold,
              color: isSelected ? Colors.blue[900] : Colors.black,
            ),
          ),
          SizedBox(height: 4.h),
          Text(
            contact.number,
            style: TextStyle(fontSize: 14.sp, color: Colors.grey[600]),
          ),
        ],
      ),
    );
  }

  BoxDecoration _recipientDecoration() {
    return BoxDecoration(
      color: isSelected ? Colors.blue[50] : null,
      border: Border(bottom: BorderSide(color: Colors.grey.shade200, width: 1)),
    );
  }
}
