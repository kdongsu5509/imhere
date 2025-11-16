import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:iamhere/geofence/view_model/geofence_view_model.dart';
import 'package:iamhere/geofence/view_model/geofence_view_model_interface.dart';

final geofenceViewModelInterfaceProvider = Provider<GeofenceViewModelInterface>(
  (ref) {
    return ref.watch(geofenceViewModelProvider.notifier);
  },
);
