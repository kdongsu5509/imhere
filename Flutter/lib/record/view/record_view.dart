import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/FlexibleScreen.dart';

class RecordView extends StatelessWidget {
  const RecordView({super.key});

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내가 보낸 메시지";
    final pageDescription = "자동으로 전송된 기록";
    final pageInfoCount = "3개 전송됨";
    return Column(
      children: [
        Expanded(
          flex: 1,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              SizedBox(height: FlexibleScreen.getHeight(context) * 0.025),
              Text(
                pageTitle,
                style: Theme.of(context).textTheme.headlineMedium,
                textWidthBasis: TextWidthBasis.parent,
              ),
              Text(
                pageDescription,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              SizedBox(height: FlexibleScreen.getHeight(context) * 0.0025),

              Text(
                pageInfoCount,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).primaryColor,
                  fontWeight: FontWeight.bold,
                  fontSize: FlexibleScreen.getHeight(context) * 0.02,
                ),
                textWidthBasis: TextWidthBasis.parent,
              ),
              SizedBox(height: FlexibleScreen.getHeight(context) * 0.03),
            ],
          ),
        ),
        Expanded(flex: 4, child: Container(color: Colors.black)),
      ],
    );
  }
}
