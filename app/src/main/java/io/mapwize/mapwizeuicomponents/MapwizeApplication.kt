package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizesdk.core.MapwizeConfiguration

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val config = MapwizeConfiguration.Builder(
                this,
                resources.getString(R.string.mapwize_api_key)
        ).build()
        MapwizeConfiguration.start(config)
    }

}
