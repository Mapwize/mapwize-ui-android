package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizesdk.core.MapwizeConfiguration

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val config = MapwizeConfiguration.Builder(this, "MapwizeDevAPIKEY").build()
        MapwizeConfiguration.start(config)
    }

}