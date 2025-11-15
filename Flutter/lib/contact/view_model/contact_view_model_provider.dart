import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';
// import 'package:riverpod_annotation/riverpod_annotation.dart'; // ❌ 제거: 표준 Provider 사용

import 'contact_view_model.dart';
import 'contact_view_model_interface.dart';

final contactViewModelInterfaceProvider = Provider<ContactViewModelInterface>((
  ref,
) {
  return ref.watch(contactViewModelProvider.notifier);
});
