package io.mapwize.mapwizeui;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.PlaceDetails;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.PlacePreview;

interface BaseUIView {


    void showSearchBar();
    void hideSearchBar();
    void showDirectionSearchBar();
    void hideDirectionSearchBar();
    void showOutOfVenueTitle();
    void showVenueTitle(String title);
    void showVenueTitleLoading(String title);
    void showDirectionButton();
    void hideDirectionButton();
    void showLanguagesSelector(List<String> languages);
    void hideLanguagesSelector();
    void showUniversesSelector(List<Universe> universes);
    void hideUniversesSelector();
    void showPlacePreviewInfo(PlacePreview preview, String language);
    void showPlaceInfoFromPreview(Place place, PlaceDetails placeDetails, String language);
    void showPlaceInfo(Place place, PlaceDetails placeDetails, String language);
    void showPlacelistInfo(Placelist placelist, String language);
    void hideInfo();
    void showSearchLoading();
    void hideSearchLoading();
    void showVenueLoading();
    void hideVenueLoading();
    void showSearch();
    void hideSearch();
    void showAccessibleFloors(List<Floor> floors);
    void showLoadingFloor(Floor floor);
    void showActiveFloor(Floor floor);
    void showSearchResultsList();
    void hideSearchResultsList();
    void showCurrentLocationInResult();
    void hideCurrentLocationInResult();
    void showSearchResults(List<? extends MapwizeObject> results);
    void showSearchResults(List<? extends MapwizeObject> results, List<Universe> universes, Universe universe);
    void showSearchDirectionFrom();
    void showSearchDirectionTo();
    void showSelectedDirectionFrom(DirectionPoint from, String language);
    void showSelectedDirectionTo(DirectionPoint to, String language);
    void showAccessibleDirectionModes(List<DirectionMode> modes);
    void showSelectedDirectionMode(DirectionMode mode);
    void showSwapButton();
    void hideSwapButton();
    void showDirectionLoading();
    void showDirectionInfo(Direction direction);
    void showNavigationInfo(NavigationInfo navigationInfo);
    void showSearchDirectionLoading();
    void hideSearchDirectionLoading();
    void showErrorMessage(String message);
    void showFollowUserMode(FollowUserMode mode);
    void showDirectionError();

    void refreshSearchData();
    void dispatchFollowUserModeWithoutLocation();
    void dispatchInformationButtonClick(MapwizeObject object);
    void dispatchMapwizeReady(MapwizeMap mapwizeMap);
}
