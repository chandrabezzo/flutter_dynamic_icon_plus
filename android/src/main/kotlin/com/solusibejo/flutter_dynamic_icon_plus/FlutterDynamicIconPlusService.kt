package com.solusibejo.flutter_dynamic_icon_plus

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

class FlutterDynamicIconPlusService: Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY

    override fun onTaskRemoved(rootIntent: Intent?) {
        changeAppIcon()

        val sp = getSharedPreferences(FlutterDynamicIconPlusPlugin.pluginName, Context.MODE_PRIVATE)
        sp.edit()?.remove(FlutterDynamicIconPlusPlugin.appIcon)?.apply()

        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    private fun changeAppIcon(){
        val sp = getSharedPreferences(FlutterDynamicIconPlusPlugin.pluginName, Context.MODE_PRIVATE)
        sp.getString(FlutterDynamicIconPlusPlugin.appIcon, null).let { name ->
            val currentlyEnabled = ComponentUtil.getCurrentEnabledAlias(this)
            if(currentlyEnabled?.name != name){
                setupIcon(name, currentlyEnabled?.name)
            }
        }
    }

    private fun setupIcon(newName: String?, currentlyName: String?){
        val components: List<ComponentName> = ComponentUtil.getComponentNames(this, newName)

        for (component in components) {
            if (currentlyName != null && currentlyName == component.className) return
            Log.d(
                "setAlternateIconName",
                String.format(
                    "Changing enabled activity-alias from %s to %s",
                    currentlyName ?: "default", component.className
                )
            )
            ComponentUtil.enable(this, packageManager, component.className)
        }

        val componentsToDisable: List<ComponentName> = if (currentlyName != null) {
            listOf(
                ComponentName(packageName, currentlyName)
            )
        } else {
            ComponentUtil.getComponentNames(this, null)
        }

        for (toDisable in componentsToDisable) {
            ComponentUtil.disable(this, packageManager, toDisable.className)
        }
    }
}