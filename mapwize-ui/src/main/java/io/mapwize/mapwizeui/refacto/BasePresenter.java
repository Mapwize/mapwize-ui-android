package io.mapwize.mapwizeui.refacto;

import io.mapwize.mapwizesdk.api.Venue;

interface BasePresenter {

    void onMapLoaded();
    void onVenueEnter(Venue venue);
    void onVenueWillEnter(Venue venue);
    void onVenueExit(Venue venue);
    void onVenueEnterError(Venue venue, Throwable error);

}
