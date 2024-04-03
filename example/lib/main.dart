import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:flutter_dynamic_icon_plus/flutter_dynamic_icon_plus.dart';

void main() => runApp(const MaterialApp(home: MyApp()));

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  MyAppState createState() => MyAppState();
}

class MyAppState extends State<MyApp> {
  int batchIconNumber = 0;

  String currentIconName = "?";

  bool loading = false;
  bool showAlert = true;

  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    if (Platform.isIOS) {
      FlutterDynamicIconPlus.applicationIconBadgeNumber.then((v) {
        setState(() {
          batchIconNumber = v;
        });
      });
    }

    FlutterDynamicIconPlus.alternateIconName.then((v) {
      setState(() {
        currentIconName = v ?? "default";
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();
    return Scaffold(
      key: scaffoldKey,
      appBar: AppBar(
        title: const Text('Dynamic App Icon Plus'),
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 28),
        child: ListView(
          children: <Widget>[
            Visibility(
              visible: Platform.isIOS,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text(
                  "Current batch number: $batchIconNumber",
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineMedium,
                ),
              ),
            ),
            Visibility(
              visible: Platform.isIOS,
              child: TextField(
                controller: controller,
                inputFormatters: <TextInputFormatter>[
                  FilteringTextInputFormatter.allow(RegExp("\\d+")),
                ],
                decoration: InputDecoration(
                  border: const OutlineInputBorder(),
                  labelText: "Set Batch Icon Number",
                  suffixIcon: loading
                      ? const Padding(
                          padding: EdgeInsets.all(16.0),
                          child: CircularProgressIndicator(
                              // strokeWidth: 2,
                              ),
                        )
                      : IconButton(
                          icon: const Icon(Icons.send),
                          onPressed: () async {
                            if (Platform.isIOS) {
                              setState(() {
                                loading = true;
                              });
                              try {
                                await FlutterDynamicIconPlus
                                    .setApplicationIconBadgeNumber(
                                        int.parse(controller.text));
                                batchIconNumber = await FlutterDynamicIconPlus
                                    .applicationIconBadgeNumber;
                                if (context.mounted) {
                                  ScaffoldMessenger.of(context)
                                      .hideCurrentSnackBar();
                                  ScaffoldMessenger.of(context)
                                      .showSnackBar(const SnackBar(
                                    duration: Duration(seconds: 3),
                                    content: Text(
                                        "Successfully changed batch number"),
                                  ));
                                }
                              } on PlatformException {
                                if (context.mounted) {
                                  ScaffoldMessenger.of(context)
                                      .hideCurrentSnackBar();
                                  ScaffoldMessenger.of(context)
                                      .showSnackBar(const SnackBar(
                                    duration: Duration(seconds: 3),
                                    content:
                                        Text("Failed to change batch number"),
                                  ));
                                }
                              } catch (e) {
                                if (context.mounted) {
                                  ScaffoldMessenger.of(context)
                                      .hideCurrentSnackBar();
                                  ScaffoldMessenger.of(context)
                                      .showSnackBar(const SnackBar(
                                    duration: Duration(seconds: 3),
                                    content:
                                        Text("Failed to change batch number"),
                                  ));
                                }
                              }

                              setState(() {
                                loading = false;
                              });
                            }
                          },
                        ),
                ),
              ),
            ),
            Visibility(
              visible: Platform.isIOS,
              child: const SizedBox(
                height: 28,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                "Current Icon Name: $currentIconName",
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.headlineMedium,
              ),
            ),
            OutlinedButton.icon(
              icon: const Icon(Icons.ac_unit),
              label: const Text("Chills"),
              onPressed: () async {
                try {
                  if (await FlutterDynamicIconPlus.supportsAlternateIcons) {
                    await FlutterDynamicIconPlus.setAlternateIconName(
                      iconName: "chills",
                      blacklistBrands: ['Redmi'],
                      blacklistManufactures: ['Xiaomi'],
                      blacklistModels: ['Redmi 200A'],
                    );
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        duration: Duration(seconds: 3),
                        content: Text("App icon change successful"),
                      ));
                    }
                    FlutterDynamicIconPlus.alternateIconName.then((v) {
                      setState(() {
                        currentIconName = v ?? "`primary`";
                      });
                    });
                    return;
                  }
                } catch (_) {
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).hideCurrentSnackBar();
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                      duration: Duration(seconds: 3),
                      content: Text("Failed to change app icon"),
                    ));
                  }
                }
              },
            ),
            OutlinedButton.icon(
              icon: const Icon(Icons.ac_unit),
              label: const Text("Photos"),
              onPressed: () async {
                try {
                  if (await FlutterDynamicIconPlus.supportsAlternateIcons) {
                    await FlutterDynamicIconPlus.setAlternateIconName(
                      iconName: 'photos',
                      blacklistBrands: ['Redmi'],
                      blacklistManufactures: ['Xiaomi'],
                      blacklistModels: ['Redmi 200A'],
                    );
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        duration: Duration(seconds: 3),
                        content: Text("App icon change successful"),
                      ));
                    }
                    FlutterDynamicIconPlus.alternateIconName.then((v) {
                      setState(() {
                        currentIconName = v ?? "`primary`";
                      });
                    });
                    return;
                  }
                } catch (_) {
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).hideCurrentSnackBar();
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                      duration: Duration(seconds: 3),
                      content: Text("Failed to change app icon"),
                    ));
                  }
                }
              },
            ),
            OutlinedButton.icon(
              icon: const Icon(Icons.ac_unit),
              label: const Text("Team Fortress"),
              onPressed: () async {
                try {
                  final isSupport =
                      await FlutterDynamicIconPlus.supportsAlternateIcons;
                  debugPrint(
                      'Supports Alternate Icons: ${isSupport.toString()}');
                  if (isSupport) {
                    await FlutterDynamicIconPlus.setAlternateIconName(
                      iconName: 'teamfortress',
                      blacklistBrands: ['Redmi'],
                      blacklistManufactures: ['Xiaomi'],
                      blacklistModels: ['Redmi 200A'],
                    );
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        duration: Duration(seconds: 3),
                        content: Text("App icon change successful"),
                      ));
                    }
                    FlutterDynamicIconPlus.alternateIconName.then((v) {
                      setState(() {
                        currentIconName = v ?? "`primary`";
                      });
                    });
                    return;
                  }
                } catch (_) {
                  if (context.mounted) {
                    ScaffoldMessenger.of(context).hideCurrentSnackBar();
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                      duration: Duration(seconds: 3),
                      content: Text("Failed to change app icon"),
                    ));
                  }
                }
              },
            ),
            const SizedBox(
              height: 28,
            ),
            OutlinedButton.icon(
              icon: const Icon(Icons.restore_outlined),
              label: const Text("Restore Icon"),
              onPressed: () async {
                try {
                  if (await FlutterDynamicIconPlus.supportsAlternateIcons) {
                    await FlutterDynamicIconPlus.setAlternateIconName(
                      iconName: null,
                      blacklistBrands: ['Redmi'],
                      blacklistManufactures: ['Xiaomi'],
                      blacklistModels: ['Redmi 200A'],
                    );
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).hideCurrentSnackBar();
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                        duration: Duration(seconds: 3),
                        content: Text("App icon restore successful"),
                      ));
                    }
                    FlutterDynamicIconPlus.alternateIconName.then((v) {
                      setState(() {
                        currentIconName = v ?? "`primary`";
                      });
                    });
                    return;
                  }
                } catch (_) {}
                if (context.mounted) {
                  ScaffoldMessenger.of(context).hideCurrentSnackBar();
                  ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
                    duration: Duration(seconds: 3),
                    content: Text("Failed to change app icon"),
                  ));
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
