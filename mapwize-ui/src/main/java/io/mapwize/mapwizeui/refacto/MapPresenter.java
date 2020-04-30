package io.mapwize.mapwizeui.refacto;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;

public class MapPresenter implements BasePresenter {

    BaseFragment fragment;
    MapwizeConfiguration mapwizeConfiguration;
    // Global values
    String language;
    boolean menuButtonHidden;
    boolean followUserButtonHidden;
    boolean floorControllerHidden;
    boolean compassHidden;

    // In venue values
    MapwizeObject selectedContent;
    Venue venue;
    Universe universe;
    Floor floor;
    List<Floor> floors;
    List<Universe> universes;
    String venueLanguage;
    List<String> venueLanguages;
    DirectionMode directionMode;
    List<DirectionMode> directionModes;
    List<MapwizeObject> mainFroms;
    List<MapwizeObject> mainSearches;
    DirectionPoint from;
    DirectionPoint to;
    Direction direction;

    public MapPresenter(BaseFragment fragment, MapwizeConfiguration mapwizeConfiguration) {
        this.fragment = fragment;
        this.mapwizeConfiguration = mapwizeConfiguration;
    }

    @Override
    public void onMapLoaded() {
        fragment.showDefaultScene();
    }

    @Override
    public void onVenueEnter(Venue venue) {

    }

    @Override
    public void onVenueWillEnter(Venue venue) {

    }

    @Override
    public void onVenueExit(Venue venue) {

    }

    @Override
    public void onVenueEnterError(Venue venue, Throwable error) {

    }
}
