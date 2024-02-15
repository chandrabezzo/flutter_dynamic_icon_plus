// In order to *not* need this ignore, consider extracting the "web" version
// of your plugin as a separate package, instead of inlining it in the same
// package as the core of your plugin.
// ignore: avoid_web_libraries_in_flutter
// import 'dart:html' as html show window;

import 'package:flutter_web_plugins/flutter_web_plugins.dart';

import 'flutter_dynamic_icon_plus_platform_interface.dart';

/// A web implementation of the FlutterDynamicIconPlusPlatform of the FlutterDynamicIconPlus plugin.
class FlutterDynamicIconPlusWeb extends FlutterDynamicIconPlusPlatform {
  /// Constructs a FlutterDynamicIconPlusWeb
  FlutterDynamicIconPlusWeb();

  static void registerWith(Registrar registrar) {
    FlutterDynamicIconPlusPlatform.instance = FlutterDynamicIconPlusWeb();
  }
}
