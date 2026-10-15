package com.solusibejo.flutter_dynamic_icon_plus

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito

/*
 * Run with `./gradlew :flutter_dynamic_icon_plus:testDebugUnitTest` from `example/android/`.
 */

internal class FlutterDynamicIconPlusPluginTest {
  @Test
  fun onMethodCall_unknownMethod_returnsNotImplemented() {
    val plugin = FlutterDynamicIconPlusPlugin()

    val call = MethodCall("unknownMethod", null)
    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(call, mockResult)

    Mockito.verify(mockResult).notImplemented()
  }

  @Test
  fun onMethodCall_setAlternateIconNameWithoutActivity_returnsError() {
    val plugin = FlutterDynamicIconPlusPlugin()

    val call = MethodCall(MethodNames.setAlternateIconName, null)
    val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
    plugin.onMethodCall(call, mockResult)

    Mockito.verify(mockResult).error("500", "Activity not found", "Activity didn't attached")
  }

  @Test
  fun findAlias_matchesFullManifestAndBareNames() {
    val aliases = listOf("com.example.app.DEFAULT", "com.example.app.icon_1", "com.example.app.dev.icon_2")

    assertEquals("com.example.app.icon_1", ComponentUtil.findAlias(aliases, "com.example.app.icon_1"))
    assertEquals("com.example.app.icon_1", ComponentUtil.findAlias(aliases, ".icon_1"))
    assertEquals("com.example.app.icon_1", ComponentUtil.findAlias(aliases, "icon_1"))
    assertEquals("com.example.app.dev.icon_2", ComponentUtil.findAlias(aliases, "icon_2"))
    assertEquals("com.example.app.DEFAULT", ComponentUtil.findAlias(aliases, "DEFAULT"))
  }

  @Test
  fun findAlias_unknownOrEmptyName_returnsNull() {
    val aliases = listOf("com.example.app.icon_1")

    assertNull(ComponentUtil.findAlias(aliases, ""))
    assertNull(ComponentUtil.findAlias(aliases, "."))
    assertNull(ComponentUtil.findAlias(aliases, "MainActivity"))
    // Must not match on a partial suffix
    assertNull(ComponentUtil.findAlias(aliases, "con_1"))
  }
}
