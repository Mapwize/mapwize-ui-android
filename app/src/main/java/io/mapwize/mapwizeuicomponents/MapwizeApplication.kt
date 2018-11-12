package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AccountManager.start(this, "35b034c24b0c0859b6170dad1e33bee0")
    }

}