package io.mapwize.mapwizeui.refacto;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
import io.mapwize.mapwizesdk.map.DirectionOptions;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.NavigationException;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.OnNavigationUpdateListener;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizesdk.map.PreviewCallback;

public class MapPresenter implements BasePresenter, MapwizeMap.OnVenueEnterListener,
        MapwizeMap.OnVenueExitListener, MapwizeMap.OnUniverseChangeListener, MapwizeMap.OnFloorChangeListener,
        MapwizeMap.OnFloorsChangeListener, MapwizeMap.OnDirectionModesChangeListener, MapwizeMap.OnLanguageChangeListener,
        MapwizeMap.OnFollowUserModeChangeListener, MapwizeMap.OnClickListener {


    private enum UIState {
        DEFAULT, SEARCH, SEARCH_FROM, SEARCH_TO, DIRECTION
    }

    BaseFragment fragment;
    MapwizeConfiguration mapwizeConfiguration;
    MapOptions mapOptions;
    MapwizeApi api;
    MapwizeMap mapwizeMap;

    UIState state;
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
    List<? extends MapwizeObject> mainFroms;
    List<? extends MapwizeObject> mainSearches;
    DirectionPoint from;
    DirectionPoint to;
    Direction direction;

    List<MapwizeObject> preloadedSearchResults;

    public MapPresenter(BaseFragment fragment, MapwizeConfiguration mapwizeConfiguration, MapOptions mapOptions) {
        this.fragment = fragment;
        this.mapwizeConfiguration = mapwizeConfiguration;
        this.mapOptions = mapOptions;
        api = MapwizeApiFactory.getApi(mapwizeConfiguration);
        state = UIState.DEFAULT;
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
    public void onMapLoaded(MapwizeMap mapwizeMap) {
        fragment.showMapwizeReady(mapwizeMap);
        fragment.showDefaultScene();
        this.mapwizeMap = mapwizeMap;
        this.mapwizeMap.addOnClickListener(this);
        this.mapwizeMap.addOnVenueEnterListener(this);
        this.mapwizeMap.addOnVenueExitListener(this);
        this.mapwizeMap.addOnDirectionModesChangeListener(this);
        this.mapwizeMap.addOnUniverseChangeListener(this);
        this.mapwizeMap.addOnLanguageChangeListener(this);
        this.mapwizeMap.addOnFollowUserModeChangeListener(this);
        this.mapwizeMap.addOnFloorChangeListener(this);
        this.mapwizeMap.addOnFloorsChangeListener(this);
    }

    @Override
    public void onVenueEnter(Venue venue) {
        if (state == UIState.DIRECTION) {
            return;
        }
        this.venue = venue;
        this.venueLanguages = venue.getSupportedLanguages();
        this.venueLanguage = mapwizeMap.getLanguage();
        fragment.showVenueEntered(venue, language);
        fragment.showLanguageButton(venueLanguages);
        fragment.showDirectionButton();
    }

    @Override
    public void onVenueWillEnter(Venue venue) {
        if (state == UIState.DIRECTION) {
            return;
        }
        fragment.showVenueEntering(venue, language);
        api.getMainSearchesForVenue(venue.getId(), new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull List<MapwizeObject> object) {
                mainSearches = object;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {

            }
        });
        api.getMainFromsForVenue(venue.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(@NonNull List<Place> object) {
                mainFroms = object;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onVenueExit(Venue venue) {
        this.venue = null;
        if (state != UIState.DIRECTION) {
            fragment.showDefaultScene();
            mainFroms = new ArrayList<>();
            mainSearches = new ArrayList<>();
            unselectContent();
            fragment.hideDirectionButton();
            fragment.showLanguageButton(new ArrayList<>());
        }
    }

    @Override
    public void onVenueEnterError(Venue venue, Throwable error) {
        fragment.showErrorMessage("Cannot load this venue");
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
        fragment.showLoadingFloor(floor);
    }

    @Override
    public void onFloorChange(@Nullable Floor floor) {
        this.floor = floor;
        fragment.showActiveFloor(floor);
    }

    @Override
    public void onFloorChangeError(@Nullable Floor floor, @NonNull Throwable error) {
        fragment.showActiveFloor(null);
        fragment.showErrorMessage("Cannot load this floor");
    }

    @Override
    public void onFloorsChange(@NonNull List<Floor> floors) {
        this.floors = floors;
        fragment.showActiveFloors(floors);
    }

    @Override
    public void onLanguageChange(@NonNull String language) {
        this.venueLanguage = language;
    }

    @Override
    public void onUniversesChange(@NonNull List<Universe> universes) {
        this.universes = universes;
        fragment.showUniverseButton(universes);
    }

    @Override
    public void onUniverseWillChange(@NonNull Universe universe) {

    }

    @Override
    public void onUniverseChange(@Nullable Universe universe) {
        this.universe = universe;
        if (selectedContent != null && !selectedContent.getUniverses().contains(universe)) {
            unselectContent();
        }
    }

    @Override
    public void onUniverseChangeError(@NonNull Universe universe, @NonNull Throwable error) {
        fragment.showErrorMessage("Cannot load this universe");
    }

    @Override
    public void onClickEvent(@NonNull ClickEvent clickEvent) {
        if (clickEvent.getEventType() == ClickEvent.VENUE_CLICK) {
            mapwizeMap.centerOnVenue(clickEvent.getVenuePreview(), 300);
        }
        if (clickEvent.getEventType() == ClickEvent.PLACE_CLICK) {
            selectPlace(clickEvent.getPlacePreview());
        }
        if (clickEvent.getEventType() == ClickEvent.MAP_CLICK) {
            if (selectedContent != null) {
                unselectContent();
            }
        }
    }

    @Override
    public void onFollowUserModeChange(@NonNull FollowUserMode followUserMode) {
        fragment.showFollowUserMode(followUserMode);
    }

    public void onFollowUserModeButtonClick() {
        if (mapwizeMap.getUserLocation() == null) {
            fragment.showFollowUserModeWithoutLocation();
        }
        switch (mapwizeMap.getFollowUserMode()) {
            case NONE:
            case FOLLOW_USER_AND_HEADING:
                mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER);
                break;
            case FOLLOW_USER:
                mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER_AND_HEADING);
                break;
        }
    }

    @Override
    public void onInformationClick() {
        fragment.showInformationButtonClick(selectedContent);
    }

    @Override
    public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode) {
        this.from = from;
        this.to = to;
        this.directionMode = directionMode;
        fragment.showDirectionLoadingScene();
        fragment.showFromDirection(from, venueLanguage);
        fragment.showToDirection(to, venueLanguage);
        fragment.showDirectionModes(directionModes);
        fragment.showDirectionMode(directionMode);
        startDirection();
    }

    @Override
    public void onDirectionButtonClick() {
        fragment.showSearchDirectionScene();
        if (mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null) {
            validateDirectionFrom(mapwizeMap.getUserLocation());
        }
        if (selectedContent != null) {
            validateDirectionTo((DirectionPoint)selectedContent);
        }
        fragment.hidePlaceInfo();
        fragment.showDirectionModes(directionModes);
        validateDirectionMode(directionMode);
        if (from == null) {
            requestDirectionFrom();
        }
        else if (to == null) {
            requestDirectionTo();
        }
        else {
            tryToStartDirection();
        }
    }

    @Override
    public void onQueryClick() {
        state = UIState.SEARCH;
        fragment.showSearchScene();
        onSearchQueryChange("");
    }

    @Override
    public void onSearchBackButtonClick() {
        fragment.hideSearchScene();
    }

    @Override
    public void onSearchQueryChange(String query) {
        if (query.length() == 0 && venue == null) {
            fragment.showSearchResults(preloadedSearchResults);
            return;
        }
        if (query.length() == 0 && venue != null) {
            fragment.showSearchResults(mainSearches, universes, universe);
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
                        fragment.showSearchResults(mapwizeObjects, universes, universe);
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
        if (state == UIState.SEARCH) {
            fragment.hideSearchScene();
            selectPlace(place, universe);
        }
        if (state == UIState.SEARCH_FROM) {
            from = place;
            if (to == null) {
                state = UIState.SEARCH_TO;
                fragment.openSearchDirectionTo();
            }
            else {
                state = UIState.DIRECTION;
                startDirection();
            }
            fragment.showFromDirection(from, venueLanguage);

        }
        else if (state == UIState.SEARCH_TO) {
            to = place;
            if (from != null) {
                state = UIState.DIRECTION;
                fragment.showToDirection(to, venueLanguage);
                startDirection();
            }
            else {
                state = UIState.SEARCH_FROM;
                fragment.openSearchDirectionFrom();
                fragment.showToDirection(to, venueLanguage);
            }
        }
    }

    @Override
    public void onSearchResultVenueClick(Venue venue) {
        if (state == UIState.SEARCH) {
            fragment.hideSearchScene();
            mapwizeMap.centerOnVenue(venue, 300);
        }
    }

    @Override
    public void onSearchResultPlacelistClick(Placelist placelist) {
        if (state == UIState.SEARCH) {
            fragment.hideSearchScene();
            selectPlacelist(placelist);
        }
        if (state == UIState.SEARCH_TO) {
            to = placelist;
            fragment.showToDirection(to, venueLanguage);
        }
    }

    @Override
    public void onFloorClick(Floor floor) {
        mapwizeMap.setFloor(floor != null ? floor.getNumber() : null);
    }

    @Override
    public void onLanguageClick(String language) {
        mapwizeMap.setLanguageForVenue(language, venue);
    }

    @Override
    public void onUniverseClick(Universe universe) {
        mapwizeMap.setUniverseForVenue(universe, venue);
    }

    @Override
    public void onDirectionBackClick() {
        Log.i("Debug", "On back click " + state);
        if (state == UIState.SEARCH_FROM || state == UIState.SEARCH_TO) {
            Log.i("Debug", "" + mapwizeMap.getDirection());
            if (mapwizeMap.getDirection() != null) {
                state = UIState.DIRECTION;
                fragment.hideSearchList();
                fragment.showFromDirection(from, venueLanguage);
                fragment.showToDirection(to, venueLanguage);
            }
            else {
                quitDirection();
            }
        }
        else if (state == UIState.DIRECTION) {
            quitDirection();
        }
    }

    @Override
    public void onDirectionSwapClick() {
        swap();
    }

    @Override
    public void onDirectionFromQueryChange(String query) {
        if (state != UIState.SEARCH_FROM) {
            return;
        }
        if (query.length() == 0 && venue != null) {
            fragment.showSearchResults(mainFroms);
            return;
        }
        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);
        builder.setObjectClass(new String[]{"place"});
        builder.setVenueId(venue.getId());
        builder.setUniverseId(universe.getId());
        SearchParams params = builder.build();
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

    @Override
    public void onDirectionToQueryChange(String query) {
        if (state != UIState.SEARCH_TO) {
            return;
        }
        if (query.length() == 0 && venue != null) {
            fragment.showSearchResults(mainSearches);
            return;
        }
        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);
        builder.setObjectClass(new String[]{"place", "placeList"});
        builder.setVenueId(venue.getId());
        builder.setUniverseId(universe.getId());
        SearchParams params = builder.build();
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

    @Override
    public void onDirectionModeChange(DirectionMode mode) {
        directionMode = mode;
        if (state == UIState.DIRECTION) {
            startDirection();
        }
    }

    @Override
    public void onDirectionFromFieldGetFocus() {
        if (state != UIState.SEARCH_FROM) {
            requestDirectionFrom();
            fragment.showToDirection(to, venueLanguage);
        }
    }

    @Override
    public void onDirectionToFieldGetFocus() {
        if (state != UIState.SEARCH_TO) {
            requestDirectionTo();
            fragment.showFromDirection(from, venueLanguage);
        }
    }

    private void unselectContent() {
        mapwizeMap.removeMarkers();
        mapwizeMap.removePromotedPlaces();
        selectedContent = null;
        fragment.hidePlaceInfo();
    }

    private void selectPlace(PlacePreview preview) {
        mapwizeMap.removeMarkers();
        mapwizeMap.removePromotedPlaces();
        mapwizeMap.addMarker(preview);
        mapwizeMap.addPromotedPlace(preview);
        fragment.showPlacePreviewInfo(preview, venueLanguage);
        preview.getFullObjectAsync(new PreviewCallback<Place>() {
            @Override
            public void getObjectAsync(Place object) {
                selectedContent = object;
                fragment.showPlaceInfoFromPreview(object, venueLanguage);
                Log.i("Debug", "Language " + venueLanguage);
            }

            @Override
            public void error(Throwable t) {

            }
        });
    }

    private void selectPlace(Place place, Universe universe) {
        mapwizeMap.removeMarkers();
        mapwizeMap.removePromotedPlaces();
        selectedContent = place;
        mapwizeMap.centerOnPlace(place, 0);
        if (universe != null && !universe.equals(this.universe)) {
            mapwizeMap.setUniverse(universe);
        }
        mapwizeMap.addMarker(place);
        mapwizeMap.addPromotedPlace(place);
        fragment.showPlaceInfo(place, venueLanguage);
    }

    private void selectPlacelist(Placelist placelist) {
        mapwizeMap.removeMarkers();
        mapwizeMap.removePromotedPlaces();
        selectedContent = placelist;
        mapwizeMap.addMarkers(placelist, markers -> {

        });
        mapwizeMap.addPromotedPlaces(placelist, places -> {});
        fragment.showPlacelistInfo(placelist, venueLanguage);
    }

    private void startDirection() {
        state = UIState.DIRECTION;
        if (from instanceof MapwizeIndoorLocation) {
            fragment.showDirectionLoadingScene();
            try {
                mapwizeMap.startNavigation(to, directionMode, new DirectionOptions.Builder().build(), new OnNavigationUpdateListener() {
                    @Override
                    public boolean shouldRecomputeNavigation(@NonNull NavigationInfo navigationInfo) {
                        fragment.showNavigationInfo(navigationInfo);
                        if (navigationInfo.getLocationDelta() > 15) {
                            fragment.showDirectionLoadingScene();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void navigationWillStart() {

                    }

                    @Override
                    public void navigationDidStart() {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            fragment.showDirectionScene(mapwizeMap.getDirection());
                            promoteDirectionPoint();
                        });
                    }

                    @Override
                    public void navigationDidFail(Throwable throwable) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            fragment.showDirectionError();
                            mapwizeMap.removeMarkers();
                            mapwizeMap.removePromotedPlaces();
                            mapwizeMap.removeDirection();
                            mapwizeMap.stopNavigation();
                        });
                    }
                });
            } catch (NavigationException e) {
                e.printStackTrace();
            }
        }
        else {
            fragment.showDirectionLoadingScene();
            api.getDirection(from, to, directionMode, new ApiCallback<Direction>() {
                @Override
                public void onSuccess(@NonNull Direction direction) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mapwizeMap.setDirection(direction);
                        mapwizeMap.removePromotedPlaces();
                        new Handler(Looper.getMainLooper()).post(() -> {
                            promoteDirectionPoint();
                            fragment.showDirectionScene(direction);
                        });
                        promoteDirectionPoint();
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.showDirectionError();
                        mapwizeMap.removeMarkers();
                        mapwizeMap.removePromotedPlaces();
                        mapwizeMap.removeDirection();
                    });
                }
            });
        }
    }

    void promoteDirectionPoint() {
        mapwizeMap.removePromotedPlaces();
        mapwizeMap.removeMarkers();
        if (from instanceof Place) {
            mapwizeMap.addPromotedPlace((Place)from);
        }
        if (to instanceof Place) {
            mapwizeMap.addPromotedPlace((Place)to);
        }
        if (to instanceof Placelist) {
            mapwizeMap.addPromotedPlaces((Placelist) to, places -> {

            });
        }
    }

    void requestDirectionFrom() {
        state = UIState.SEARCH_FROM;
        fragment.openSearchDirectionFrom();
    }

    void requestDirectionTo() {
        state = UIState.SEARCH_TO;
        fragment.openSearchDirectionTo();
    }

    void validateDirectionFrom(DirectionPoint point) {
        from = point;
        fragment.showFromDirection(point, venueLanguage);
        tryToStartDirection();
    }

    void validateDirectionTo(DirectionPoint point) {
        to = point;
        fragment.showToDirection(point, venueLanguage);
        tryToStartDirection();
    }

    void validateDirectionMode(DirectionMode mode) {
        directionMode = mode;
        fragment.showDirectionMode(mode);
        tryToStartDirection();
    }

    void swap() {
        DirectionPoint tmpTo = from;
        DirectionPoint tmpFrom = to;
        from = null;
        to = null;
        validateDirectionFrom(tmpFrom);
        validateDirectionTo(tmpTo);
        if (tmpTo != null && (tmpTo instanceof Place || tmpTo instanceof Placelist)) {
            selectedContent = (MapwizeObject)tmpTo;
        }
        Log.i("Debug", "Swap");
    }

    void tryToStartDirection() {
        Log.i("Debug", "Try");
        Log.i("Debug", "from " + from);
        Log.i("Debug", "to " + to);
        if (from != null && to != null) {
            startDirection();
            Log.i("Debug", "Start direction");
        }
    }

    void quitDirection() {
        mapwizeMap.removeDirection();
        mapwizeMap.stopNavigation();
        mapwizeMap.removePromotedPlaces();
        mapwizeMap.removeMarkers();
        from = null;
        to = null;
        fragment.hideSearchDirectionScene();
        state = UIState.DEFAULT;
        if (selectedContent != null) {
            if (selectedContent instanceof Place) {
                selectPlace((Place)selectedContent, universe);
            }
            else {
                selectPlacelist((Placelist)selectedContent);
            }
        }
    }



}
