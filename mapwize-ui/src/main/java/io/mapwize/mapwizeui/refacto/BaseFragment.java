package io.mapwize.mapwizeui.refacto;

import io.mapwize.mapwizesdk.api.Venue;

interface BaseFragment {

    void showDefaultScene();

    void showInVenueScene(Venue venue, String language);

    void showVenueEntering(Venue venue, String language);
}
