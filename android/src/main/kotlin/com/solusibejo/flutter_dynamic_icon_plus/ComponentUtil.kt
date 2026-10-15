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
    /** Name of the optional activity-alias that holds the default icon. */
    private const val defaultAliasName = "DEFAULT"

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

    /** All `activity-alias` entries declared in the merged manifest, enabled or not. */
    fun aliases(context: Context): List<ActivityInfo> =
        packageInfo(context).activities?.filter { it.targetActivity != null } ?: emptyList()

    /**
     * Matches [name] against the declared alias names. Accepts the fully qualified name
     * (`com.example.app.icon_1`), the manifest form (`.icon_1`) or the bare name (`icon_1`).
     */
    fun findAlias(aliasNames: List<String>, name: String): String? {
        val simpleName = name.removePrefix(".")
        if (simpleName.isEmpty()) return null
        return aliasNames.firstOrNull { it == name || it.endsWith(".$simpleName") }
    }

    fun getCurrentEnabledAlias(context: Context): ActivityInfo? {
        val packageManager = context.packageManager
        val packageName = context.packageName
        return try {
            val info = packageInfo(context)
            var enabled: ActivityInfo? = null
            // Store activities in a local variable to make it safe for smart cast
            val activities = info.activities
            if (activities != null) {
                for (activityInfo in activities) {
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

    private fun setState(context: Context, className: String, state: Int) {
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context.packageName, className),
            state,
            PackageManager.DONT_KILL_APP
        )
    }

    /**
     * Applies the icon stored by `setAlternateIconName`, then clears it.
     * An empty stored value means "restore the default icon".
     *
     * Only activity-aliases are ever disabled. Real activities (MainActivity, or activities
     * added by other plugins) must stay enabled, otherwise aliases pointing at them, deep links
     * and notification intents stop working.
     */
    fun applyPendingIcon(context: Context) {
        val sp = context.getSharedPreferences(FlutterDynamicIconPlusPlugin.pluginName, Context.MODE_PRIVATE)
        if (!sp.contains(FlutterDynamicIconPlusPlugin.appIcon)) return

        val name = sp.getString(FlutterDynamicIconPlusPlugin.appIcon, null).orEmpty()
        sp.edit().remove(FlutterDynamicIconPlusPlugin.appIcon).apply()

        val aliases = aliases(context)
        val aliasNames = aliases.map { it.name }

        // Versions <= 1.5.0 disabled the alias target (MainActivity), which makes every alias
        // unlaunchable. Put it back to its manifest state.
        aliases.mapNotNull { it.targetActivity }.distinct().forEach {
            setState(context, it, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        }

        // Unknown names (e.g. "MainActivity") fall back to the default icon.
        val target = findAlias(aliasNames, name) ?: findAlias(aliasNames, defaultAliasName)
        Log.d("applyPendingIcon", "Requested '$name', enabling ${target ?: "MainActivity (no .DEFAULT alias)"}")

        // Enable the new entry before disabling the others so the app always has a launcher entry.
        target?.let { setState(context, it, PackageManager.COMPONENT_ENABLED_STATE_ENABLED) }
        aliasNames.filter { it != target }.forEach {
            setState(context, it, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        }
    }
}
