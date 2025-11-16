import 'package:flutter/material.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:geolocator/geolocator.dart';
import 'package:iamhere/geofence/service/my_location_service.dart';

class MapSelectView extends StatefulWidget {
  final NLatLng? initialLocation;

  const MapSelectView({super.key, this.initialLocation});

  @override
  State<MapSelectView> createState() => _MapSelectViewState();
}

class _MapSelectViewState extends State<MapSelectView> {
  late NaverMapController _mapController;
  late Future<NaverMapViewOptions> _mapOptionsFuture;
  NMarker? _currentMarker;
  NLatLng? _selectedLocation;
  NLatLng? _initialTarget;

  @override
  void initState() {
    super.initState();
    _selectedLocation = widget.initialLocation; // 기존 선택 위치 설정
    _mapOptionsFuture = _getInitialMapOptions();
  }

  Future<NaverMapViewOptions> _getInitialMapOptions() async {
    Position currentUserLocation = await MyLocationService()
        .getCurrentUserLocation();

    // 기존 선택 위치가 있으면 그곳을, 없으면 현재 위치
    _initialTarget =
        widget.initialLocation ??
        NLatLng(currentUserLocation.latitude, currentUserLocation.longitude);

    return NaverMapViewOptions(
      initialCameraPosition: NCameraPosition(target: _initialTarget!, zoom: 16),
      scrollGesturesEnable: true,
      zoomGesturesEnable: true,
      tiltGesturesEnable: true,
      consumeSymbolTapEvents: false,
    );
  }

  void _updateMarker(NLatLng location) {
    if (_currentMarker != null) {
      _mapController.deleteOverlay(_currentMarker!.info);
    }

    _currentMarker = NMarker(id: "location_marker", position: location);

    _mapController.addOverlay(_currentMarker!);
  }

  void _confirmSelection() {
    debugPrint('=== _confirmSelection 호출됨 ===');
    debugPrint('_selectedLocation: $_selectedLocation');

    if (_selectedLocation != null) {
      debugPrint(
        '위치 반환 시도: ${_selectedLocation!.latitude}, ${_selectedLocation!.longitude}',
      );
      Navigator.of(context).pop(_selectedLocation);
      debugPrint('Navigator.pop 완료');
    } else {
      debugPrint('선택된 위치 없음 - SnackBar 표시');
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('위치를 선택해주세요')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder<NaverMapViewOptions>(
        future: _mapOptionsFuture,
        builder: (context, snapshot) {
          if (snapshot.hasError) {
            return _buildErrorWidget(snapshot);
          }
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasData) {
            return _buildMainMapWidget(snapshot, context);
          }
          return const Center(child: Text('지도 로딩 준비 중...'));
        },
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  Stack _buildMainMapWidget(
    AsyncSnapshot<NaverMapViewOptions> snapshot,
    BuildContext context,
  ) {
    return Stack(children: [_naverMap(snapshot), _finishedButton(context)]);
  }

  Positioned _finishedButton(BuildContext context) {
    return Positioned(
      bottom: 24,
      left: 16,
      right: 80,
      child: ElevatedButton(
        onPressed: () {
          debugPrint('완료 버튼 클릭됨!');
          _confirmSelection();
        },
        style: ElevatedButton.styleFrom(
          backgroundColor: Theme.of(context).primaryColor,
          padding: EdgeInsets.symmetric(vertical: 16.h),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12.r),
          ),
          elevation: 4,
        ),
        child: Text(
          '이 위치로 선택',
          style: TextStyle(
            color: Colors.white,
            fontSize: 16.sp,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }

  NaverMap _naverMap(AsyncSnapshot<NaverMapViewOptions> snapshot) {
    return NaverMap(
      options: snapshot.data!,
      onMapReady: (controller) {
        _mapController = controller;
        debugPrint("Naver map is ready!");

        if (widget.initialLocation != null) {
          _updateMarker(widget.initialLocation!);
        }
      },
      onMapTapped: (NPoint point, NLatLng latlng) {
        setState(() {
          _selectedLocation = latlng;
        });
        _updateMarker(latlng);
        debugPrint(
          "선택된 위치 - lat: ${latlng.latitude}, lng: ${latlng.longitude}",
        );
      },
      onCameraChange: (NCameraUpdateReason reason, bool animated) {
        if (reason == NCameraUpdateReason.gesture) {
          debugPrint("지도가 제스처로 이동됨");
        }
      },
    );
  }

  Center _buildErrorWidget(AsyncSnapshot<NaverMapViewOptions> snapshot) {
    return Center(
      child: Text(
        '위치 정보를 가져오는 데 실패했습니다.\n${snapshot.error}',
        textAlign: TextAlign.center,
        style: const TextStyle(color: Colors.red),
      ),
    );
  }
}
