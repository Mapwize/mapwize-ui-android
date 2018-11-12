package io.mapwize.mapwizeuicomponents

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.mapwize.mapwizecomponents.ui.MapwizeFragment
import io.mapwize.mapwizeformapbox.api.MapwizeObject
import io.mapwize.mapwizeformapbox.api.Place
import io.mapwize.mapwizeformapbox.map.MapOptions
import io.mapwize.mapwizeformapbox.map.MapwizePlugin
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MapwizeFragment.OnFragmentInteractionListener, MapwizeFragment.UIBehaviour {


    private var mapwizeFragment: MapwizeFragment? = null
    private var mapboxMap: MapboxMap? = null
    private var mapwizePlugin: MapwizePlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val opts = MapOptions.Builder()
                .restrictContentToVenue("56b20714c3fa800b00d8f0b5")
                .centerOnPlace("5bc49413bf0ed600114db27c").build()
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
    }

    override fun onMenuButtonClick() {
        Log.i("Debug", "Menu click")
    }

    override fun onInformationButtonClick(place: Place?) {
        Log.i("Debug", "Info click " + place?.name)
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
