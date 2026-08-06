// swift-tools-version:5.7
import PackageDescription

let package = Package(
    name: "flutter_dynamic_icon_plus",
    platforms: [
        .iOS(.v11),
        .macOS(.v10_14),
    ],
    products: [
        .library(
            name: "flutter_dynamic_icon_plus",
            targets: ["flutter_dynamic_icon_plus"]
        ),
    ],
    targets: [
        .target(
            name: "flutter_dynamic_icon_plus",
            path: "ios/Classes",
            swiftSettings: [
                .define("SWIFT_PACKAGE")
            ]
        )
    ]
)
