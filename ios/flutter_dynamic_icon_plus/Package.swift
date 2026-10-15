// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "flutter_dynamic_icon_plus",
    platforms: [
        .iOS("13.0")
    ],
    products: [
        .library(name: "flutter-dynamic-icon-plus", targets: ["flutter_dynamic_icon_plus"])
    ],
    dependencies: [
        .package(name: "FlutterFramework", path: "../FlutterFramework")
    ],
    targets: [
        .target(
            name: "flutter_dynamic_icon_plus",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework")
            ]
        )
    ]
)
