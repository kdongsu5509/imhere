import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/FlexibleScreen.dart';

class FriendsView extends StatelessWidget {
  const FriendsView({super.key});

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내 친구 목록";
    final pageDescription = "메시지를 받을 친구들";
    final pageInfoCount = "4명 등록됨";
    return Column(
      children: [
        Expanded(
          flex: 2,
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
              Container(
                height: FlexibleScreen.getHeight(context) * 0.06,
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor,
                  borderRadius: BorderRadius.all(
                    Radius.circular(FlexibleScreen.getWidth(context) * 0.1),
                  ),
                ),
                child: Center(
                  child: Text(
                    "폰에서 친구 연락처 불러오기",
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      color: Theme.of(context).colorScheme.surface,
                      fontWeight: FontWeight.bold,
                      fontSize: FlexibleScreen.getHeight(context) * 0.019,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        Expanded(flex: 5, child: Container(color: Colors.black)),
      ],
    );
  }
}
