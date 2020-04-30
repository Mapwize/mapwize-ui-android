package io.mapwize.mapwizeui.refacto;

import java.util.List;

import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

interface BaseFragment {

    void showDefaultScene();

    /*void showInVenueScene(Venue venue,
                          List<Universe> universes, Universe universe,
                          List<Floor> floors, Floor floor,
                          List<String> languages, String language);
    */
}
