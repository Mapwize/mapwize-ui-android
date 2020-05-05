package io.mapwize.mapwizeui.refacto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;

interface BasePresenter {

    void onMapLoaded();
    void onVenueEnter(Venue venue);
    void onVenueWillEnter(Venue venue);
    void onVenueExit(Venue venue);
    void onVenueEnterError(Venue venue, Throwable error);
    void onDirectionModesChange(@NonNull List<DirectionMode> list);
    void onFloorWillChange(@Nullable Floor floor);
    void onFloorChange(@Nullable Floor floor);
    void onFloorsChange(@NonNull List<Floor> list);
    void onLanguageChange(@NonNull String s);
    void onUniversesChange(@NonNull List<Universe> list);
    void onUniverseWillChange(@NonNull Universe universe);
    void onUniverseChange(@Nullable Universe universe);
    void onClickEvent(@NonNull ClickEvent clickEvent);
    void onFollowUserModeChange(@NonNull FollowUserMode followUserMode);
    void onDirectionButtonClick();
    void onQueryClick();
    void onSearchBackButtonClick();
    void onSearchQueryChange(String query);
    void onSearchResultPlaceClick(Place place, Universe universe);
    void onSearchResultVenueClick(Venue venue);
    void onSearchResultPlacelistClick(Placelist placelist);
}
