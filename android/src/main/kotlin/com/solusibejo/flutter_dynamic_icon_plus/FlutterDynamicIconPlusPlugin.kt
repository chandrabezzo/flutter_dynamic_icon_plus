package com.solusibejo.flutter_dynamic_icon_plus

import android.app.Activity
import android.content.ComponentName
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** FlutterDynamicIconPlusPlugin */
class FlutterDynamicIconPlusPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: Activity? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_dynamic_icon_plus")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when(call.method){
      MethodNames.setAlternateIconName -> {
        if(activity != null){
          val packageManager = activity!!.packageManager
          val packageName = activity!!.packageName
          val iconName = call.argument<String?>(Arguments.iconName)
          val default = "MainActivity"
          val currentlyEnabled = ComponentUtil.getCurrentEnabledAlias(activity!!)

          if (currentlyEnabled == null && iconName == null) {
            // Currently enabled and request to enable activities are both the default activities
            result.success(true)
          }

          val components: List<ComponentName> = ComponentUtil.getComponentNames(activity!!, iconName)
          for (component in components) {
            if (currentlyEnabled != null && currentlyEnabled.name.equals(component.className)) return
            Log.d(
              "setAlternateIconName",
              String.format(
                "Changing enabled activity-alias from %s to %s",
                if (currentlyEnabled != null) currentlyEnabled.name else "default",
                component.className
              )
            )
            ComponentUtil.enable(activity!!, packageManager, component.className)
          }

          val componentsToDisable: List<ComponentName> = if (currentlyEnabled != null) {
            listOf(
              ComponentName(
                packageName,
                currentlyEnabled.name
              )
            )
          } else {
            ComponentUtil.getComponentNames(activity!!, null)
          }

          for (toDisable in componentsToDisable) {
            ComponentUtil.disable(activity!!, packageManager, toDisable.className)
          }
          result.success(true)
        }
        else {
          result.error("500", "Activity not found", "Activity didn't attached")
        }
      }
      MethodNames.supportsAlternateIcons -> {
        if(activity != null){
          val packageInfo = ComponentUtil.packageInfo(activity!!)
          //By default, we have one activity (MainActivity).
          //If there is more than one activity, it indicates that we have an alternative activity
          val isAlternateAvailable = packageInfo.activities.size > 1
          result.success(isAlternateAvailable)
        }
        else {
          result.success(false)
        }
      }
      MethodNames.getAlternateIconName -> {
        if(activity != null){
          val enabledComponent = ComponentUtil.getCurrentEnabledAlias(activity!!)
          result.success(enabledComponent?.name)
        }
        else {
          result.error("500", "Activity not found", "Activity didn't attached")
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }
}
