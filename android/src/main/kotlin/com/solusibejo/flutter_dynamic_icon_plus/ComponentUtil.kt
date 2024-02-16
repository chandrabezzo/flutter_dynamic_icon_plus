package com.solusibejo.flutter_dynamic_icon_plus

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build


object ComponentUtil {
    fun enable(
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

    fun disable(
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

    fun enabledComponent(packageManager: PackageManager, packageName: String): ActivityInfo? {
        val packageInfo = packageInfo(
            packageManager,
            packageName,
            PackageManager.GET_ACTIVITIES or PackageManager.GET_DISABLED_COMPONENTS
        )
        return packageInfo.activities.find { it.isEnabled }
    }

    fun isComponentEnabled(pm: PackageManager, pkgName: String?, clsName: String): Boolean {
        val componentName = ComponentName(pkgName!!, clsName)
        return when (pm.getComponentEnabledSetting(componentName)) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ->       // We need to get the application info to get the component's default state
                try {
                    val packageInfo = packageInfo(pm, pkgName, PackageManager.GET_ACTIVITIES)
                    val components = ArrayList<ComponentInfo>()
                    if (packageInfo.activities != null) components.addAll(packageInfo.activities)
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
                val packageInfo = packageInfo(pm, pkgName, PackageManager.GET_ACTIVITIES)
                val components = ArrayList<ComponentInfo>()
                if (packageInfo.activities != null) components.addAll(packageInfo.activities)
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

    fun packageInfo(packageManager: PackageManager, packageName: String, component: Int): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(
                component.toLong()))
        } else {
            @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName,
                component
            )
        }
    }
}