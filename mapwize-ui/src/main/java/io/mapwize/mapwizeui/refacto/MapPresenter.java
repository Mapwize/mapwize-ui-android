package io.mapwize.mapwizeui.refacto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;

public class MapPresenter implements BasePresenter {

    BaseFragment fragment;
    MapwizeConfiguration mapwizeConfiguration;
    // Global values
    String language = "en";
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
        this.venue = venue;
        this.venueLanguages = venue.getSupportedLanguages();
        fragment.showInVenueScene(venue, language);
    }

    @Override
    public void onVenueWillEnter(Venue venue) {
        fragment.showVenueEntering(venue, language);
    }

    @Override
    public void onVenueExit(Venue venue) {
        this.venue = null;
        fragment.showDefaultScene();
    }

    @Override
    public void onVenueEnterError(Venue venue, Throwable error) {

    }

    @Override
    public void onDirectionModesChange(@NonNull List<DirectionMode> directionModes) {
        this.directionModes = directionModes;
        if (!directionModes.isEmpty() && !directionModes.contains(directionMode)) {
            directionMode = directionModes.get(0);
        }
    }

    @Override
    public void onFloorWillChange(@Nullable Floor floor) {

    }

    @Override
    public void onFloorChange(@Nullable Floor floor) {
        this.floor = floor;
        fragment.setActiveFloor(floor);
    }

    @Override
    public void onFloorsChange(@NonNull List<Floor> floors) {
        this.floors = floors;
        fragment.setActiveFloors(floors);
    }

    @Override
    public void onLanguageChange(@NonNull String language) {
        this.venueLanguage = language;
    }

    @Override
    public void onUniversesChange(@NonNull List<Universe> universes) {
        this.universes = universes;
    }

    @Override
    public void onUniverseWillChange(@NonNull Universe universe) {

    }

    @Override
    public void onUniverseChange(@Nullable Universe universe) {
        this.universe = universe;
    }

    @Override
    public void onClickEvent(@NonNull ClickEvent clickEvent) {

    }

    @Override
    public void onFollowUserModeChange(@NonNull FollowUserMode followUserMode) {

    }

    @Override
    public void onDirectionButtonClick() {

    }

    @Override
    public void onQueryClick() {
        fragment.showSearchScene();
    }

    @Override
    public void onSearchBackButtonClick() {
        if (venue == null) {
            fragment.backFromSearchScene();
        }
        else {
            fragment.showInVenueScene(venue, language);
        }
    }
}
