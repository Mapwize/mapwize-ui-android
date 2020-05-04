package io.mapwize.mapwizeui.refacto;

import java.util.List;

import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizeui.FloorControllerView;
import io.mapwize.mapwizeui.R;

interface BaseFragment {

    void showDefaultScene();

    void showInVenueScene(Venue venue, String language);

    void showVenueEntering(Venue venue, String language);

    void showSearchScene();

    void backFromSearchScene();

    void setActiveFloors(List<Floor> floors);

    void setActiveFloor(Floor floor);
}
