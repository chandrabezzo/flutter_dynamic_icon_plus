package com.solusibejo.flutter_dynamic_icon_plus

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log


object ComponentUtil {
    private fun enable(
        context: Context,
        packageManager: PackageManager,
        componentNameString: String,
    ) {
        val componentName = ComponentName(context, componentNameString)

        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun disable(
        context: Context,
        packageManager: PackageManager,
        componentNameString: String,
    ) {
        val componentName = ComponentName(context, componentNameString)

        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun packageInfo(context: Context): PackageInfo {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val component = PackageManager.GET_ACTIVITIES or PackageManager.GET_DISABLED_COMPONENTS

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(
                component.toLong()))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName,
                component
            )
        }
    }

    fun getCurrentEnabledAlias(context: Context): ActivityInfo? {
        val packageManager = context.packageManager
        val packageName = context.packageName
        return try {
            val info = packageInfo(context)

            var enabled: ActivityInfo? = null
            if (info.activities != null) {
                for (activityInfo in info.activities) {
                    // Only checks among the `activity-alias`s, for current enabled alias
                    if (activityInfo.targetActivity != null) {
                        val isEnabled: Boolean =
                            isComponentEnabled(context, packageManager, packageName, activityInfo.name)
                        if (isEnabled) enabled = activityInfo
                    }
                }
            }
            enabled
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun isComponentEnabled(context: Context, pm: PackageManager, pkgName: String?, clsName: String): Boolean {
        val componentName = ComponentName(pkgName!!, clsName)
        return when (pm.getComponentEnabledSetting(componentName)) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ->                 // We need to get the application info to get the component's default state
                try {
                    val packageInfo = packageInfo(context)
                    val components = ArrayList<ComponentInfo>()
                    if (packageInfo.activities != null) {
                        packageInfo.activities?.let { components.addAll(it) }
                    }

                    for (componentInfo in components) {
                        if (componentInfo.name == clsName) {
                            return componentInfo.isEnabled
                        }
                    }

                    // the component is not declared in the AndroidManifest
                    false
                } catch (e: PackageManager.NameNotFoundException) {
                    // the package isn't installed on the device
                    false
                }

            else -> try {
                val packageInfo = packageInfo(context)
                val components = ArrayList<ComponentInfo>()
                if (packageInfo.activities != null) {
                    packageInfo.activities?.let { components.addAll(it) }
                }
                for (componentInfo in components) {
                    if (componentInfo.name == clsName) {
                        return componentInfo.isEnabled
                    }
                }
                false
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    private fun getComponentNames(context: Context, activityName: String?): List<ComponentName> {
        val packageName = context.packageName
        if (activityName == null) {
            val pm = context.packageManager
            val components = ArrayList<ComponentName>()
            try {
                val info = pm.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES or PackageManager.GET_DISABLED_COMPONENTS
                )
                if (info.activities != null) {
                    for (activityInfo in info.activities) {
                        if (activityInfo.targetActivity == null) {
                            components.add(ComponentName(packageName, activityInfo.name))
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // the package isn't installed on the device
                Log.d("getComponentNames", "the package isn't installed on the device")
            }
            return components
        }
        val componentName = String.format("%s.%s", packageName, activityName)
        val component = ComponentName(packageName, componentName)
        val components = ArrayList<ComponentName>()
        components.add(component)
        return components
    }

    fun changeAppIcon(context: Context, packageManager: PackageManager, packageName: String){
        val sp = context.getSharedPreferences(FlutterDynamicIconPlusPlugin.pluginName, Context.MODE_PRIVATE)
        sp.getString(FlutterDynamicIconPlusPlugin.appIcon, null).let { name ->
            val currentlyEnabled = getCurrentEnabledAlias(context)
            Log.d("changeAppIcon", "Currently Enabled: $currentlyEnabled")
            Log.d("changeAppIcon", "Will Enabled: $name")

            if(name != null){
                if(name.isNotEmpty()){
                    if(currentlyEnabled?.name != name){
                        setupIcon(context, packageManager, packageName, name, currentlyEnabled?.name)
                    }
                }
            }
        }
    }

    fun removeCurrentAppIcon(context: Context){
        val sp = context.getSharedPreferences(FlutterDynamicIconPlusPlugin.pluginName, Context.MODE_PRIVATE)
        sp.edit()?.remove(FlutterDynamicIconPlusPlugin.appIcon)?.apply()
    }

    private fun setupIcon(context: Context, packageManager: PackageManager, packageName: String, newName: String?, currentlyName: String?){
        val components: List<ComponentName> = getComponentNames(context, newName)

        for (component in components) {
            if (currentlyName != null && currentlyName == component.className) return
            Log.d(
                "setAlternateIconName",
                String.format(
                    "Changing enabled activity-alias from %s to %s",
                    currentlyName ?: "default", component.className
                )
            )
            enable(context, packageManager, component.className)
        }

        val componentsToDisable: List<ComponentName> = if (currentlyName != null) {
            listOf(
                ComponentName(packageName, currentlyName)
            )
        } else {
            getComponentNames(context, null)
        }

        for (toDisable in componentsToDisable) {
            disable(context, packageManager, toDisable.className)
        }
    }
}