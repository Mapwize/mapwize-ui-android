package io.mapwize.mapwizeui.refacto;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeApi;
import io.mapwize.mapwizesdk.api.MapwizeApiFactory;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.SearchParams;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;

public class MapPresenter implements BasePresenter {

    BaseFragment fragment;
    MapwizeConfiguration mapwizeConfiguration;
    MapOptions mapOptions;
    MapwizeApi api;
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

    List<MapwizeObject> preloadedSearchResults;

    public MapPresenter(BaseFragment fragment, MapwizeConfiguration mapwizeConfiguration, MapOptions mapOptions) {
        this.fragment = fragment;
        this.mapwizeConfiguration = mapwizeConfiguration;
        this.mapOptions = mapOptions;
        api = MapwizeApiFactory.getApi(mapwizeConfiguration);
        preloadVenueSearchResults();
    }

    private void preloadVenueSearchResults() {
        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery("");
        // Filter by object type
        builder.setObjectClass(new String[]{"venue"});
        // Filter by organization if present in map options
        builder.setOrganizationId(mapOptions.getRestrictContentToOrganizationId());
        // Filter by venue if present in map options
        if (mapOptions.getRestrictContentToVenueIds() != null) {
            builder.setVenueIds(mapOptions.getRestrictContentToVenueIds());
        }
        SearchParams params = builder.build();
        // Api call
        api.search(params, new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                // Display the result
                preloadedSearchResults = mapwizeObjects;
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
            }
        });
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

    @Override
    public void onSearchQueryChange(String query) {
        if (query.length() == 0) {
            // Perform main searches
            if (venue == null) {
                fragment.showSearchResults(preloadedSearchResults);
            }
            return;
        }

        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);

        // If we are in a venue, search for venue content
        if (venue != null) {
            // Filter object by type
            builder.setObjectClass(new String[]{"place", "placeList"});
            // Filter object for the current venue
            builder.setVenueId(venue.getId());

            SearchParams params = builder.build();
            // Api Call
            api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.showSearchResults(mapwizeObjects);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
        // If we are not in a venue, search for venue
        else {
            // Filter by object type
            builder.setObjectClass(new String[]{"venue"});
            // Filter by organization if present in map options
            builder.setOrganizationId(mapOptions.getRestrictContentToOrganizationId());
            // Filter by venue if present in map options
            if (mapOptions.getRestrictContentToVenueIds() != null) {
                builder.setVenueIds(mapOptions.getRestrictContentToVenueIds());
            }
            SearchParams params = builder.build();
            // Api call
            api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                       fragment.showSearchResults(mapwizeObjects);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
    }

    @Override
    public void onSearchResultPlaceClick(Place place, Universe universe) {
        fragment.backFromSearchScene();
        selectPlace(place, universe);
    }

    @Override
    public void onSearchResultVenueClick(Venue venue) {
        fragment.backFromSearchScene();
        fragment.centerOnVenue(venue);
    }

    @Override
    public void onSearchResultPlacelistClick(Placelist placelist) {
        fragment.backFromSearchScene();
        selectPlacelist(placelist);
    }

    private void selectPlace(Place place, Universe universe) {
        Log.i("Debug", "Select place");
        selectedContent = place;
        fragment.centerOnPlace(place, universe);
    }

    private void selectPlacelist(Placelist placelist) {
        Log.i("Debug", "Select placelist");
    }
}
