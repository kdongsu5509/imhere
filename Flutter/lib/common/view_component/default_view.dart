import 'package:flutter/material.dart';

class DefaultView extends StatelessWidget {
  final String _appTitle = 'Imhere';
  final Widget child;
  const DefaultView({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(_appTitle,
                style: theme.textTheme.headlineLarge?.copyWith(
                    fontSize: MediaQuery.of(context).size.width * 0.065
                )
            ),
            Row(
              children: [
                IconButton(onPressed: (){}, icon: theme == ThemeMode.light ? Icon(Icons.dark_mode) : Icon(Icons.light_mode)),
                IconButton(onPressed: () {}, icon: Icon(Icons.logout_outlined))
              ],
            )
        ],),
      ),
      body: Center(
        child: child,
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.location_on), label: '지오펜스'),
          BottomNavigationBarItem(icon: Icon(Icons.people), label: '연락처'),
          BottomNavigationBarItem(icon: Icon(Icons.history), label: '기록'),
        ],
      ),
    );
  }
}