import 'flutter_dynamic_icon_plus_platform_interface.dart';
export 'src/consts/arguments.dart';
export 'src/consts/method_names.dart';

class FlutterDynamicIconPlus {
  static Future<bool> get supportsAlternateIcons async =>
      await FlutterDynamicIconPlusPlatform.instance.supportsAlternateIcons;

  static Future<String?> get alternateIconName async =>
      await FlutterDynamicIconPlusPlatform.instance.alternateIconName;

  /// `blacklistBrands`, `blacklistManufactures`, `blacklistModels` just only
  /// work for Android platform only
  static Future<void> setAlternateIconName({
    String? iconName,
    List<String> blacklistBrands = const [],
    List<String> blacklistManufactures = const [],
    List<String> blacklistModels = const [],
  }) async {
    await FlutterDynamicIconPlusPlatform.instance.setAlternateIconName(
      iconName: iconName,
      blacklistBrands: blacklistBrands,
      blacklistManufactures: blacklistManufactures,
      blacklistModels: blacklistModels,
    );
  }

  static Future<int> get applicationIconBadgeNumber async =>
      await FlutterDynamicIconPlusPlatform.instance.applicationIconBadgeNumber;

  static Future<void> setApplicationIconBadgeNumber(
          int batchIconNumber) async =>
      await FlutterDynamicIconPlusPlatform.instance
          .setApplicationIconBadgeNumber(batchIconNumber);
}
