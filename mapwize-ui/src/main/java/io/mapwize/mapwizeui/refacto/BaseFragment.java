package io.mapwize.mapwizeui.refacto;

import java.util.List;

import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizeui.FloorControllerView;
import io.mapwize.mapwizeui.R;

interface BaseFragment {

    void showDefaultScene();

    void showInVenueScene(Venue venue, String language);

    void showVenueEntering(Venue venue, String language);

    void showSearchScene();

    void backToDefaultScene();

    void backToVenueScene(Venue venue, String language);

    void showActiveFloors(List<Floor> floors);

    void showLoadingFloor(Floor floor);

    void showActiveFloor(Floor floor);

    void showSearchResults(List<? extends MapwizeObject> results);

    void showErrorMessage(String message);
}
