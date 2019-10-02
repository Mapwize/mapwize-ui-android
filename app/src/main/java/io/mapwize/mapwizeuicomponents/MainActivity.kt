package io.mapwize.mapwizeuicomponents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.indoorlocation.core.IndoorLocation
import io.indoorlocation.manual.ManualIndoorLocationProvider
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings
import io.mapwize.mapwizeformapbox.api.Floor
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizeMap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener {

    private var mapwizeFragment: MapwizeFragment? = null
    private var mapwizeMap: MapwizeMap? = null
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

        // Uncomment and change value to test different settings configuration
        var uiSettings = MapwizeFragmentUISettings.Builder()
                //.menuButtonHidden(true)
                //.followUserButtonHidden(false)
                //.floorControllerHidden(false)
                //.compassHidden(true)
                .build()
        mapwizeFragment = MapwizeFragment.newInstance(opts, uiSettings)
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(fragmentContainer.id, mapwizeFragment!!)
        ft.commit()

    }

    /**
     * Fragment listener
     */
    override fun onFragmentReady(mapwizeMap: MapwizeMap) {
        this.mapwizeMap = mapwizeMap

        this.locationProvider = ManualIndoorLocationProvider()
        this.mapwizeMap?.setIndoorLocationProvider(this.locationProvider!!)

        this.mapwizeMap?.addOnLongClickListener {
            val indoorLocation = IndoorLocation("manual_provider", it.latLngFloor.latitude, it.latLngFloor.longitude, it.latLngFloor.floor, System.currentTimeMillis())
            this.locationProvider?.setIndoorLocation(indoorLocation)
        }
    }

    override fun onMenuButtonClick() {

    }

    override fun onInformationButtonClick(mapwizeObject: MapwizeObject?) {

    }

    override fun onFollowUserButtonClickWithoutLocation() {
        Log.i("Debug", "onFollowUserButtonClickWithoutLocation")
    }

    override fun shouldDisplayInformationButton(mapwizeObject: MapwizeObject?): Boolean {
        Log.i("Debug", "shouldDisplayInformationButton")
        when (mapwizeObject) {
            is Place -> return true
        }
        return false
    }

    override fun shouldDisplayFloorController(floors: MutableList<Floor>?): Boolean {
        Log.i("Debug", "shouldDisplayFloorController")
        if (floors == null || floors.size <= 1) {
            return false
        }
        return true
    }

}
