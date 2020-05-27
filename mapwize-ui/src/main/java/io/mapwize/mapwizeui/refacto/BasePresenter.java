package io.mapwize.mapwizeui.refacto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapwizeMap;

interface BasePresenter {

    void onMapLoaded(MapwizeMap mapwizeMap);
    void onDirectionButtonClick();
    void onQueryClick();
    void onSearchBackButtonClick();
    void onSearchQueryChange(String query);
    void onSearchResultPlaceClick(Place place, Universe universe);
    void onSearchResultVenueClick(Venue venue);
    void onSearchResultPlacelistClick(Placelist placelist);
    void onSearchResultCurrentLocationClick();
    void onFloorClick(Floor floor);
    void onLanguageClick(String language);
    void onUniverseClick(Universe universe);
    void onDirectionBackClick();
    void onDirectionSwapClick();
    void onDirectionFromQueryChange(String query);
    void onDirectionToQueryChange(String query);
    void onDirectionModeChange(DirectionMode mode);
    void onDirectionFromFieldGetFocus();
    void onDirectionToFieldGetFocus();
    void onFollowUserModeButtonClick();
    void onInformationClick();
    void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode);
}
