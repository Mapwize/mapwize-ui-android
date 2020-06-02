package io.mapwize.mapwizeui;

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

    void showSearchBar();
    void hideSearchBar();
    void showDirectionSearchBar();
    void hideDirectionSearchBar();
    void showOutOfVenueTitle();
    void showVenueTitle(String title);
    void showVenueTitleLoading(String title);
    void showDirectionButton();
    void hideDirectionButton();
    void showLanguagesSelector();
    void hideLanguagesSelector();
    void showUniversesSelector();
    void hideUniversesSelector();
    void showPlacePreviewInfo(PlacePreview preview, String language);
    void showPlaceInfoFromPreview(Place place, String language);
    void showPlaceInfo(Place place, String language);
    void showPlacelistInfo(Placelist placelist, String language);
    void hideInfo();

    void showLoading();

    void hideLoading();

    void showSearchDirectionLoading();

    void hideSearchDirectionLoading();


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

    void openSearchDirectionFrom(boolean showCurrentLocation);

    void openSearchDirectionTo();

    void hideSearchList();

    void setAccessibleLanguages(List<String> languages);

    void setAccessibleUniverses(List<Universe> universes);

    void showActiveFloors(List<Floor> floors);

    void showLoadingFloor(Floor floor);

    void showActiveFloor(Floor floor);

    void showSearchResults(List<? extends MapwizeObject> results);

    void showSearchResults(List<? extends MapwizeObject> results, List<Universe> universes, Universe universe);

    void showErrorMessage(String message);

    void showFollowUserMode(FollowUserMode mode);

    void showFollowUserModeWithoutLocation();

    void showInformationButtonClick(MapwizeObject object);

    void showMapwizeReady(MapwizeMap mapwizeMap);

    void showDirectionError();
}
