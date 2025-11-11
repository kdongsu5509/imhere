import 'package:flutter/material.dart';
import 'package:iamhere/friends/view/friends_view.dart';
import 'package:iamhere/geofence/view/geofence_view.dart';
import 'package:iamhere/record/view/record_view.dart';

class DefaultView extends StatefulWidget {
  final Widget child;
  const DefaultView({super.key, required this.child});

  @override
  State<DefaultView> createState() => _DefaultViewState();
}

class _DefaultViewState extends State<DefaultView> {
  final String _appTitle = 'Imhere';

  int _selectedIndex = 0;

  final List<Widget> _widgetOptions = <Widget>[
    const Center(child: GeofenceView()),
    const Center(child: FriendsView()),
    const Center(child: RecordView()),
  ];

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: _buildAppBar(context, theme),
      body: _buildBodyWithPadding(
        context,
        _widgetOptions.elementAt(_selectedIndex),
      ),
      bottomNavigationBar: _buildBottomNavigationBar(
        _selectedIndex,
        _onItemTapped,
      ),
      floatingActionButton: FloatingActionButton(
        shape: const CircleBorder(),
        child: Icon(
          Icons.add_rounded,
          color: Theme.of(context).colorScheme.surface,
        ),
        onPressed: () {
          print("구현하자~~~~~~");
        },
      ),
    );
  }

  PreferredSize _buildAppBar(BuildContext context, ThemeData theme) {
    return PreferredSize(
      preferredSize: const Size.fromHeight(kToolbarHeight),
      child: AppBar(
        centerTitle: false,
        backgroundColor: Colors.transparent,
        elevation: 0.0,
        automaticallyImplyLeading: false,
        titleSpacing: 0,
        title: Padding(
          padding: EdgeInsets.all(MediaQuery.of(context).size.width * 0.04),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              _buildImHereAsTitle(context, theme),
              _buildAppBarButton(theme),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildImHereAsTitle(BuildContext context, ThemeData theme) {
    return Text(
      _appTitle,
      style: theme.textTheme.headlineLarge?.copyWith(
        fontSize: MediaQuery.of(context).size.width * 0.075,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Row _buildAppBarButton(ThemeData theme) {
    return Row(
      children: [
        IconButton(
          onPressed: () {},
          icon: const Icon(Icons.dark_mode_outlined),
        ),
        IconButton(onPressed: () {}, icon: const Icon(Icons.logout_outlined)),
      ],
    );
  }

  Padding _buildBodyWithPadding(BuildContext context, Widget bodyWidget) {
    return Padding(
      padding: EdgeInsets.symmetric(
        horizontal: MediaQuery.of(context).size.width * 0.04,
      ),
      child: Column(
        children: [
          const Divider(height: 1, thickness: 0.5, color: Colors.grey),
          Expanded(
            child: bodyWidget, // 인자로 받은 위젯 사용
          ),
        ],
      ),
    );
  }

  BottomNavigationBar _buildBottomNavigationBar(
    int currentIndex,
    Function(int) onTap,
  ) {
    return BottomNavigationBar(
      items: const [
        BottomNavigationBarItem(
          icon: Icon(Icons.location_on_outlined),
          label: '지오펜스',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.people_outline_outlined),
          label: '연락처',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.history), label: '기록'),
      ],
      currentIndex: currentIndex,
      onTap: onTap,
    );
  }
}
