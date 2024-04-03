package com.solusibejo.flutter_dynamic_icon_plus

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FlutterDynamicIconPlusService: Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY

    override fun onTaskRemoved(rootIntent: Intent?) {
        ComponentUtil.changeAppIcon(this, packageManager, packageName)

        ComponentUtil.removeCurrentAppIcon(this)

        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        ComponentUtil.changeAppIcon(this, packageManager, packageName)

        ComponentUtil.removeCurrentAppIcon(this)

        super.onDestroy()
        stopSelf()
    }
}