package io.mapwize.mapwizeuicomponents

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.indoorlocation.core.IndoorLocation
import io.indoorlocation.manual.ManualIndoorLocationProvider
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.ClickEvent
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizePlugin
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener, MapwizeFragment.UIBehaviour {


    private var mapwizeFragment: MapwizeFragment? = null
    private var mapboxMap: MapboxMap? = null
    private var mapwizePlugin: MapwizePlugin? = null
    private var locationProvider: ManualIndoorLocationProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Uncomment and fill place holder to test MapwizeUI on your venue
        val opts = MapOptions.Builder()
                //.restrictContentToOrganization("YOUR_ORGANIZATION_ID")
                //.restrictContentToVenue("YOUR_VENUE_ID")
                //.centerOnVenue("YOUR_VENUE_ID")
                //.centerOnPlace("YOUR_PLACE_ID")
                .build()
        mapwizeFragment = MapwizeFragment.newInstance(opts)
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(fragmentContainer.id, mapwizeFragment!!)
        ft.commit()

    }

    /**
     * Fragment listener
     */
    override fun onFragmentReady(mapboxMap: MapboxMap?, mapwizePlugin: MapwizePlugin?) {
        this.mapboxMap = mapboxMap
        this.mapwizePlugin = mapwizePlugin
        this.mapwizeFragment?.componentsFunctions = this

        this.locationProvider = ManualIndoorLocationProvider()
        this.mapwizePlugin?.setLocationProvider(this.locationProvider!!)

        this.mapwizePlugin?.addOnLongClickListener {
            val indoorLocation = IndoorLocation("manual_provider", it.latLngFloor.latitude, it.latLngFloor.longitude, it.latLngFloor.floor, System.currentTimeMillis())
            this.locationProvider?.setIndoorLocation(indoorLocation)
        }
    }

    override fun onMenuButtonClick() {
        // TODO Do something with menu click
    }

    override fun onInformationButtonClick(place: Place?) {
        // TODO Do something with information button
    }

    /**
     * UIBehaviour
     */
    override fun shouldDisplayInformationButton(mapwizeObject: MapwizeObject?): Boolean {
        when (mapwizeObject) {
            is Place -> return true
        }
        return false
    }

}
