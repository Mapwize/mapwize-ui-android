package io.mapwize.mapwizeui.refacto;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.PlacePreview;

interface BaseFragment {

    void showDefaultScene();

    void showVenueEntered(Venue venue, String language);

    void showVenueEntering(Venue venue, String language);

    void showPlacePreviewInfo(PlacePreview preview, String language);

    void showPlaceInfoFromPreview(Place place, String language);

    void showPlaceInfo(Place place, String language);

    void showPlacelistInfo(Placelist placelist, String language);

    void hidePlaceInfo();

    void showSearchScene();

    void hideSearchScene();

    void showSearchDirectionScene();

    void showDirectionLoadingScene();

    void showDirectionScene(Direction direction);

    void showNavigationInfo(NavigationInfo navigationInfo);

    void hideSearchDirectionScene();

    void showFromDirection(DirectionPoint from, String language);

    void showToDirection(DirectionPoint to, String language);

    void showDirectionModes(List<DirectionMode> modes);

    void showDirectionMode(DirectionMode mode);

    public void openSearchDirectionFrom(boolean showCurrentLocation);

    void openSearchDirectionTo();

    void hideSearchList();

    void showLanguageButton(List<String> languages);

    void showUniverseButton(List<Universe> universes);

    void showActiveFloors(List<Floor> floors);

    void showLoadingFloor(Floor floor);

    void showActiveFloor(Floor floor);

    void showDirectionButton();

    void hideDirectionButton();

    void showSearchResults(List<? extends MapwizeObject> results);

    void showSearchResults(List<? extends MapwizeObject> results, List<Universe> universes, Universe universe);

    void showErrorMessage(String message);

    void showFollowUserMode(FollowUserMode mode);

    void showFollowUserModeWithoutLocation();

    void showInformationButtonClick(MapwizeObject object);

    void showMapwizeReady(MapwizeMap mapwizeMap);

    void showDirectionError();
}
