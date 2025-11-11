import 'package:flutter/material.dart';
import 'package:iamhere/common/view_component/FlexibleScreen.dart';

class GeofenceView extends StatefulWidget {
  const GeofenceView({super.key});

  @override
  State<GeofenceView> createState() => _GeofenceViewState();
}

class _GeofenceViewState extends State<GeofenceView>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500), // 깜빡이는 속도 (1.5sec)
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final pageTitle = "내 위치 기반 알림";
    final pageDescription = "특정 위치에 도착하면 친구에게 자동으로 메시지를 보냅니다";
    final pageInfoCount = "2개 등록됨";
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
              SizedBox(height: FlexibleScreen.getHeight(context) * 0.02),
              Container(
                height: FlexibleScreen.getHeight(context) * 0.05,
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor,
                  borderRadius: BorderRadius.all(
                    Radius.circular(FlexibleScreen.getWidth(context) * 0.05),
                  ),
                ),
                child: Row(
                  children: [
                    FadeTransition(
                      opacity: _controller,
                      child: Padding(
                        padding: EdgeInsets.fromLTRB(
                          FlexibleScreen.getHeight(context) * 0.02,
                          0,
                          FlexibleScreen.getHeight(context) * 0.01,
                          0,
                        ),
                        child: Icon(
                          Icons.location_on_outlined,
                          color: Colors.red,
                        ),
                      ),
                    ),
                    Text(
                      "위치 추적 중이에요",
                      style: Theme.of(context).textTheme.headlineMedium
                          ?.copyWith(
                            color: Theme.of(context).colorScheme.surface,
                            fontWeight: FontWeight.bold,
                            fontSize:
                                FlexibleScreen.getHeight(context) * 0.0165,
                          ),
                    ),
                  ],
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
