package io.mapwize.mapwizeuicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.mapwize.mapwizeui.MapwizeFragment

class HomeFragment(var mapwizeFragment: MapwizeFragment): Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fm = childFragmentManager
        val ft = fm.beginTransaction()
        ft.add(R.id.childFragmentContainer, mapwizeFragment)
        ft.commit()
    }
}