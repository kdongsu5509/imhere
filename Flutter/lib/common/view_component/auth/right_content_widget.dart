import 'package:flutter/material.dart';

Widget rightContentWidget({required BuildContext context, required String right}) {
  final width = MediaQuery.of(context).size.width;
  final height = MediaQuery.of(context).size.height;

  int length = right.length;
  int maxLength = 6;
  if (length > maxLength) {
    length = maxLength;
  }

  return Padding(
    padding: EdgeInsets.symmetric(horizontal: width * 0.01),
    child: ClipRRect(
      borderRadius: BorderRadius.circular(35.0),
      child: Container(
        width: width * 0.05 * length,
        height: height * 0.04,
        decoration: BoxDecoration(
          color: Colors.grey[100],
          borderRadius: BorderRadius.circular(35.0),
        ),
        child: Center(child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(right, style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                fontSize: width * 0.03
            ),),
          ],
        )),
      ),
    ),
  );
}