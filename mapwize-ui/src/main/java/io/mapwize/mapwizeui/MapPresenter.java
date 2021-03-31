package io.mapwize.mapwizeui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.LatLngFloor;
import io.mapwize.mapwizesdk.api.LatLngFloorInVenue;
import io.mapwize.mapwizesdk.api.MapwizeApi;
import io.mapwize.mapwizesdk.api.MapwizeApiFactory;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.PlaceDetails;
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
import io.mapwize.mapwizesdk.map.Marker;
import io.mapwize.mapwizesdk.map.MarkerOptions;
import io.mapwize.mapwizesdk.map.NavigationException;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.OnNavigationUpdateListener;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizesdk.map.PreviewCallback;
import io.mapwize.mapwizesdk.map.VenuePreview;
import io.mapwize.mapwizeui.events.Channel;
import io.mapwize.mapwizeui.events.EventManager;

import static io.mapwize.mapwizesdk.map.MapwizeConstants.DEFAULT_DIRECTION_END_MARKER_NAME;
import static io.mapwize.mapwizesdk.map.MapwizeConstants.DEFAULT_DIRECTION_START_MARKER_NAME;

public class MapPresenter implements BasePresenter, MapwizeMap.OnVenueEnterListener,
        MapwizeMap.OnVenueExitListener, MapwizeMap.OnUniverseChangeListener, MapwizeMap.OnFloorChangeListener,
        MapwizeMap.OnFloorsChangeListener, MapwizeMap.OnDirectionModesChangeListener, MapwizeMap.OnLanguageChangeListener,
        MapwizeMap.OnFollowUserModeChangeListener, MapwizeMap.OnClickListener {


    private String lastPlacePreviewId = "";
    private PlacePreview selectedPlacePreview = null;

    private enum UIState {
        DEFAULT, SEARCH, SEARCH_FROM, SEARCH_TO, DIRECTION
    }

    private BaseUIView fragment;
    private MapwizeConfiguration mapwizeConfiguration;
    private MapOptions mapOptions;
    private MapwizeApi api;
    private MapwizeMap mapwizeMap;

    private UIState state;
    // Global values
    private String language = "en";
    private String lastQuery = "";

    // In venue values
    private MapwizeObject selectedContent;
    private Venue venue;
    private Universe universe;
    private Floor floor;
    private List<Floor> floors;
    private List<Universe> universes;
    private String venueLanguage;
    private List<String> venueLanguages;
    private DirectionMode directionMode;
    private List<DirectionMode> directionModes;
    private List<? extends MapwizeObject> mainFroms = new ArrayList<>();
    private List<? extends MapwizeObject> mainSearches = new ArrayList<>();
    private DirectionPoint from;
    private DirectionPoint to;
    private Direction direction;

    private List<MapwizeObject> preloadedSearchResults;

    MapPresenter(BaseUIView fragment, MapwizeConfiguration mapwizeConfiguration, MapOptions mapOptions) {
        this.fragment = fragment;
        this.mapwizeConfiguration = mapwizeConfiguration;
        this.mapOptions = mapOptions;
        api = MapwizeApiFactory.getApi(mapwizeConfiguration);
        setState(UIState.DEFAULT);
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
                preloadedSearchResults = new ArrayList<>();
            }
        });
    }

    public static final String venue_KEY = "venueKey";

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        this.venue = venue;
        this.venueLanguages = venue.getSupportedLanguages();
        this.venueLanguage = mapwizeMap.getLanguage();
        fragment.showDirectionButton();
        fragment.showVenueTitle(venue.getTranslation(language).getTitle());
        fragment.hideVenueLoading();
        if (state == UIState.DIRECTION) {
            return;
        }
        fragment.showLanguagesSelector(venueLanguages);
    }

    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {
        api.getMainSearchesForVenue(venue.getId(), new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull List<MapwizeObject> object) {
                mainSearches = object;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                mainSearches = new ArrayList<>();
            }
        });
        api.getMainFromsForVenue(venue.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(@NonNull List<Place> object) {
                mainFroms = object;
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                mainFroms = new ArrayList<>();
            }
        });
        if (state == UIState.DIRECTION) {
            return;
        }
        fragment.showVenueTitleLoading(venue.getTranslation(language).getTitle());
        fragment.showVenueLoading();
    }

    @Override
    public void onVenueExit(@NonNull Venue venue) {
        this.venue = null;
        if (state != UIState.DIRECTION) {
            fragment.showOutOfVenueTitle();
            fragment.hideUniversesSelector();
            fragment.hideLanguagesSelector();
            fragment.hideDirectionSearchBar();
            fragment.showSearchBar();
            mainFroms = new ArrayList<>();
            mainSearches = new ArrayList<>();
            unselectContent();
            fragment.hideDirectionButton();
        }
    }

    @Override
    public void onVenueEnterError(@NonNull Venue venue, @NonNull Throwable error) {
        fragment.showErrorMessage("Cannot load this venue");
    }

    @Override
    public void onDirectionModesChange(@NonNull List<DirectionMode> directionModes) {
        this.directionModes = directionModes;
        if (!directionModes.isEmpty() && !directionModes.contains(directionMode)) {
            directionMode = directionModes.get(0);
        }
        fragment.showAccessibleDirectionModes(directionModes);
        fragment.showSelectedDirectionMode(directionMode);
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
        fragment.showAccessibleFloors(floors);
    }

    @Override
    public void onLanguageChange(@NonNull String language) {
        this.venueLanguage = language;
        fragment.setLanguage(language);
        //searchResultList.setListener(this);
    }

    @Override
    public void onUniversesChange(@NonNull List<Universe> universes) {
        this.universes = universes;
        if (state != UIState.DIRECTION) {
            fragment.showUniversesSelector(universes);
        }
    }

    @Override
    public void onUniverseWillChange(@NonNull Universe universe) {
        fragment.showVenueLoading();
    }

    @Override
    public void onUniverseChange(@Nullable Universe universe) {
        this.universe = universe;
        if (selectedContent != null && !selectedContent.getUniverses().contains(universe)) {
            unselectContent();
        }
        fragment.hideVenueLoading();
    }

    @Override
    public void onUniverseChangeError(@NonNull Universe universe, @NonNull Throwable error) {
        fragment.showErrorMessage("Cannot load this universe");
    }

    @Override
    public void onClickEvent(@NonNull ClickEvent clickEvent) {
        if (state == UIState.DIRECTION) {
            return;
        }
        if (clickEvent.getEventType() == ClickEvent.VENUE_CLICK) {
            VenuePreview venuePreview = clickEvent.getVenuePreview();
            if (venuePreview != null) {
                mapwizeMap.centerOnVenue(venuePreview, 300);
            }
        }
        if (clickEvent.getEventType() == ClickEvent.PLACE_CLICK) {
            selectedPlacePreview = clickEvent.getPlacePreview();
            if (selectedPlacePreview != null) {
                String newId = selectedPlacePreview.getId();
                if (selectedContent == null || !lastPlacePreviewId.equals(newId)) {
                    lastPlacePreviewId = selectedPlacePreview.getId();
                    selectPlace(selectedPlacePreview);
                }
            }

        }
        if (clickEvent.getEventType() == ClickEvent.MAP_CLICK) {
            if (selectedContent != null) {
                unselectContent();
            } else if (selectedPlacePreview != null) {
                unselectContent();
            }
        }
    }

    @Override
    public void onFollowUserModeChange(@NonNull FollowUserMode followUserMode) {
        fragment.showFollowUserMode(followUserMode);
    }

    public void onFollowUserModeButtonClick() {
        if (mapwizeMap == null) {
            return;
        }
        if (mapwizeMap.getUserLocation() == null) {
            fragment.dispatchFollowUserModeWithoutLocation();
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
        fragment.dispatchInformationButtonClick(selectedContent);
    }

    @Override
    public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode) {
        this.from = from;
        this.to = to;
        this.directionMode = directionMode;
        fragment.showSelectedDirectionFrom(from, venueLanguage);
        fragment.showSelectedDirectionTo(to, venueLanguage);
        fragment.showAccessibleDirectionModes(directionModes);
        fragment.showSelectedDirectionMode(directionMode);
        fragment.hideSearchBar();
        fragment.showDirectionSearchBar();
        fragment.hideUniversesSelector();
        fragment.hideLanguagesSelector();
        startDirection();
    }

    @Override
    public void selectPlace(Place place, boolean centerOn) {
        mapwizeMap.removeMarkers();
        if (!place.getUniverses().contains(this.universe)) {
            mapwizeMap.setUniverse(place.getUniverses().get(0));
        }
        selectedContent = place;
        mapwizeMap.centerOnPlace(place, 0);
        mapwizeMap.selectPlace(place);
        api.getPlaceDetails(place.getId(), new ApiCallback<PlaceDetails>(){
            @Override
            public void onSuccess(@NonNull PlaceDetails placeDetails) {
                new Handler(Looper.getMainLooper()).post(
                        ()-> fragment.showPlaceInfo(place, placeDetails, venueLanguage));
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                new Handler(Looper.getMainLooper()).post(
                        () -> fragment.showPlaceInfo(place, null, venueLanguage));
            }
        });
    }

    @Override
    public void grantAccess(String accessKey, ApiCallback<Boolean> callback) {
        mapwizeMap.grantAccess(accessKey, new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean object) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(object);
                    preloadVenueSearchResults();
                });
            }

            @Override
            public void onFailure(@Nullable Throwable t) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onFailure(t);
                });
            }
        });
    }

    @Override
    public void refreshSearchData() {
        preloadVenueSearchResults();
    }

    @Override
    public boolean onBackButtonPressed() {
        if (state == UIState.SEARCH_FROM || state == UIState.SEARCH_TO || state == UIState.DIRECTION) {
            onDirectionBackClick();
            return true;
        } else if (state == UIState.SEARCH) {
            onSearchBackButtonClick();
            return true;
        }
        return false;
    }

    @Override
    public void onDirectionButtonClick() {
        fragment.showDirectionSearchBar();
        fragment.hideSearchBar();
        if (mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null) {
            validateDirectionFrom(mapwizeMap.getUserLocation());
        }
        if (selectedContent != null) {
            validateDirectionTo((DirectionPoint) selectedContent);
        } else if (selectedPlacePreview != null) {
            validateDirectionTo((DirectionPoint) selectedPlacePreview);
        }
        fragment.hideInfo();
        fragment.showAccessibleDirectionModes(directionModes);
        validateDirectionMode(directionMode);
        if (from == null) {
            requestDirectionFrom();
        } else if (to == null) {
            requestDirectionTo();
        }
    }

    public static final String universes_KEY = "universesKey";

    @Override
    public void onSearchBackButtonClick() {
        fragment.hideSearch();
        setState(UIState.DEFAULT);
    }

    public static final String universe_KEY = "universeKey";

    @Override
    public void onSearchResultPlaceClick(Place place, Universe universe) {
        if (state == UIState.SEARCH) {
            EventManager.getInstance().triggerOnContentSelect(place, this.universe, universe, lastQuery.length() == 0 ? Channel.MAIN_SEARCHES : Channel.SEARCH, lastQuery);
            fragment.hideSearch();
            selectPlace(place, universe);
            setState(UIState.DEFAULT);
        }
        if (state == UIState.SEARCH_FROM) {
            from = place;
            if (to == null) {
                requestDirectionTo();
            } else {
                setState(UIState.DIRECTION);
                startDirection();
            }
            fragment.showSelectedDirectionFrom(from, venueLanguage);

        } else if (state == UIState.SEARCH_TO) {
            to = place;
            if (from != null) {
                setState(UIState.DIRECTION);
                startDirection();
            } else {
                requestDirectionFrom();
            }
            fragment.showSelectedDirectionTo(to, venueLanguage);
        }
    }

    @Override
    public void onSearchResultVenueClick(Venue venue) {
        if (state == UIState.SEARCH) {
            fragment.hideSearch();
            mapwizeMap.centerOnVenue(venue, 300);
            setState(UIState.DEFAULT);
        }
    }

    @Override
    public void onSearchResultPlacelistClick(Placelist placelist) {
        if (state == UIState.SEARCH) {
            EventManager.getInstance().triggerOnContentSelect(placelist, universe, universe, lastQuery.length() == 0 ? Channel.MAIN_SEARCHES : Channel.SEARCH, lastQuery);
            fragment.hideSearch();
            selectPlacelist(placelist);
            setState(UIState.DEFAULT);
        }
        if (state == UIState.SEARCH_TO) {
            to = placelist;
            if (from != null) {
                setState(UIState.DIRECTION);
                startDirection();
            } else {
                requestDirectionFrom();
            }
            fragment.showSelectedDirectionTo(to, venueLanguage);
        }
    }

    @Override
    public void onSearchResultCurrentLocationClick() {
        from = mapwizeMap.getUserLocation();
        if (to == null) {
            requestDirectionTo();
        } else {
            setState(UIState.DIRECTION);
            startDirection();
        }
        fragment.showSelectedDirectionFrom(from, venueLanguage);
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
        if (state == UIState.SEARCH_FROM || state == UIState.SEARCH_TO) {
            if (mapwizeMap.getDirection() != null) {
                setState(UIState.DIRECTION);
                fragment.hideSearchResultsList();
                fragment.showSelectedDirectionFrom(from, venueLanguage);
                fragment.showSelectedDirectionTo(to, venueLanguage);
            } else {
                quitDirection();
            }
        } else if (state == UIState.DIRECTION) {
            quitDirection();
        }
    }

    @Override
    public void onDirectionSwapClick() {
        swap();
    }

    public static final String state_KEY = "stateKey";
    public static final String from_KEY = "fromKey";

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
            fragment.showSelectedDirectionTo(to, venueLanguage);
        }
    }

    @Override
    public void onDirectionToFieldGetFocus() {
        if (state != UIState.SEARCH_TO) {
            requestDirectionTo();
            fragment.showSelectedDirectionFrom(from, venueLanguage);
        }
    }

    @Override
    public void unselectContent() {
        mapwizeMap.removeMarkers();
        mapwizeMap.unselectPlace();
        selectedContent = null;
        selectedPlacePreview = null;
        fragment.hideInfo();
    }

    @Override
    public String getFloor() {
        if (mapwizeMap != null && mapwizeMap.getFloor() != null) {
            return mapwizeMap.getFloor().getName();
        }
        return "";
    }

    @Override
    public MapwizeMap getMapwizeMap() {
        return mapwizeMap;
    }

    static boolean notified = false;
    private void selectPlace(PlacePreview preview) {
        selectedPlacePreview = preview;
        mapwizeMap.removeMarkers();
        mapwizeMap.selectPlace(preview);
        fragment.showPlacePreviewInfo(preview, venueLanguage);
        preview.getFullObjectAsync(new PreviewCallback<Place>() {
            @Override
            public void getObjectAsync(Place object) {
                api.getPlaceDetails(object.getId(), new ApiCallback<PlaceDetails>(){
                    @Override
                    public void onSuccess(@NonNull PlaceDetails placeDetails) {
                        fragment.showPlaceInfoFromPreview(object, placeDetails, venueLanguage);
                        selectedContent = object;
                        EventManager.getInstance().triggerOnContentSelect(
                                object, mapwizeMap.getUniverse(),
                                mapwizeMap.getUniverse(),
                                Channel.MAP_CLICK,
                                null);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        if (!notified) {
                            notified = true;
                            fragment.showErrorMessage("Can't get more details");
                        }
                        t.printStackTrace();
                        fragment.showPlaceInfoFromPreview(object, null, venueLanguage);
                        selectedContent = object;
                        EventManager.getInstance().triggerOnContentSelect(
                                object, mapwizeMap.getUniverse(),
                                mapwizeMap.getUniverse(),
                                Channel.MAP_CLICK,
                                null);
                    }
                });

            }

            @Override
            public void error(Throwable t) {
                if (!notified) {
                    notified = true;
                    fragment.showErrorMessage("Can't get more details");
                }
                t.printStackTrace();
                fragment.showPreviewOnly(selectedPlacePreview);
            }
        });
    }

    private void selectPlace(Place place, Universe universe) {
        mapwizeMap.removeMarkers();
        selectedContent = place;
        mapwizeMap.centerOnPlace(place, 0);
        if (universe != null && !universe.equals(this.universe)) {
            mapwizeMap.setUniverse(universe);
        } else if (!place.getUniverses().contains(this.universe)) {
            mapwizeMap.setUniverse(place.getUniverses().get(0));
        }
        mapwizeMap.selectPlace(place);
        api.getPlaceDetails(place.getId(), new ApiCallback<PlaceDetails>(){
            @Override
            public void onSuccess(@NonNull PlaceDetails placeDetails) {
                new Handler(Looper.getMainLooper()).post(
                        ()-> fragment.showPlaceInfo(place, placeDetails, venueLanguage));
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                new Handler(Looper.getMainLooper()).post(
                        () -> fragment.showPlaceInfo(place, null, venueLanguage));
            }
        });
    }

    private void selectPlacelist(Placelist placelist) {
        mapwizeMap.removeMarkers();
        mapwizeMap.unselectPlace();
        selectedContent = placelist;
        mapwizeMap.getMapwizeApi().getPlacesForPlacelist(placelist.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(@NonNull List<Place> places) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    List<Marker> markers = new ArrayList<>();
                    for (Place place : places) {
                        markers.add(Marker.createMarker(place, new MarkerOptions.Builder().build()));
                    }
                    mapwizeMap.addMarkers(markers);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        fragment.showPlacelistInfo(placelist, venueLanguage);
    }

    public static final String to_KEY = "toKey";

    void requestDirectionFrom() {
        setState(UIState.SEARCH_FROM);
        fragment.hideSwapButton();
        fragment.showSearchResultsList();
        if (mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null) {
            fragment.showCurrentLocationInResult();
        }
        fragment.showSearchDirectionFrom();
    }

    void requestDirectionTo() {
        setState(UIState.SEARCH_TO);
        fragment.hideSwapButton();
        fragment.hideCurrentLocationInResult();
        fragment.showSearchResultsList();
        fragment.showSearchDirectionTo();
    }

    void validateDirectionFrom(DirectionPoint point) {
        from = point;
        fragment.showSelectedDirectionFrom(point, venueLanguage);
        tryToStartDirection();
    }

    void validateDirectionTo(DirectionPoint point) {
        to = point;
        fragment.showSelectedDirectionTo(point, venueLanguage);
        tryToStartDirection();
    }

    void validateDirectionMode(DirectionMode mode) {
        directionMode = mode;
        fragment.showSelectedDirectionMode(mode);
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
            selectedContent = (MapwizeObject) tmpTo;
        }
    }

    void tryToStartDirection() {
        if (from != null && to != null) {
            startDirection();
        }
    }

    void quitDirection() {
        mapwizeMap.removeDirection();
        mapwizeMap.stopNavigation();
        mapwizeMap.removeMarkers();
        if (to instanceof Place || to instanceof Placelist) {
            selectedContent = (MapwizeObject) to;
        } else if (to instanceof PlacePreview) {
            selectedPlacePreview = (PlacePreview) to;
        }
        from = null;
        to = null;

        fragment.showSearchBar();
        fragment.hideDirectionSearchBar();
        fragment.showUniversesSelector(universes);
        fragment.showLanguagesSelector(venueLanguages);
        fragment.hideSearchResultsList();
        fragment.hideInfo();
        fragment.showSelectedDirectionFrom(null, null);
        fragment.showSelectedDirectionTo(null, null);

        setState(UIState.DEFAULT);
        if (selectedContent != null) {
            if (selectedContent instanceof Place) {
                selectPlace((Place) selectedContent, universe);
            } else {
                selectPlacelist((Placelist) selectedContent);
            }
        } else if (selectedPlacePreview != null) {
            selectPlace(selectedPlacePreview);
        }
    }

    public boolean isBackEnabled() {
        return state == MapPresenter.UIState.SEARCH_FROM || state == MapPresenter.UIState.SEARCH_TO || state == MapPresenter.UIState.DIRECTION || state == MapPresenter.UIState.SEARCH;
    }

    public static final String directionMode_KEY = "directionModeKey";
    public static final String lastQuery_KEY = "lastQueryKey";

    @Override
    public void onMapLoaded(MapwizeMap mapwizeMap) {
        fragment.dispatchMapwizeReady(mapwizeMap);
        if (mapOptions.getCenterOnPlaceId() != null) {
            api.getPlace(mapOptions.getCenterOnPlaceId(), new ApiCallback<Place>() {
                @Override
                public void onSuccess(@NonNull Place place) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        selectPlace(place, universe);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {

                }
            });
        }
        if (this.state == UIState.DEFAULT) {
            fragment.showOutOfVenueTitle();
            fragment.showSearchBar();
            fragment.hideLanguagesSelector();
            fragment.hideUniversesSelector();
            fragment.hideDirectionSearchBar();
        }

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
    public void onQueryClick() {
        if (this.state == UIState.SEARCH) {
            return;
        }
        setState(UIState.SEARCH);
        fragment.showSearch();
        onSearchQueryChange("");
    }

    @Override
    public void onSearchQueryChange(String query) {
        if (lastQuery != null && lastQuery.equals(query) && !lastQuery.equals("")) {
            return;
        }
        lastQuery = query;
        if (query.length() == 0 && venue == null) {
            fragment.showSearchResults(preloadedSearchResults);
            return;
        }
        if (query.length() == 0) {
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
            fragment.showSearchLoading();
            api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.showSearchResults(mapwizeObjects, universes, universe);
                        fragment.hideSearchLoading();
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.hideSearchLoading();
                    });
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
            fragment.showSearchLoading();
            api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.showSearchResults(mapwizeObjects);
                        fragment.hideSearchLoading();
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.hideSearchLoading();
                    });
                }
            });
        }
    }

    @Override
    public void onDirectionFromQueryChange(String query) {
        if (lastQuery != null && lastQuery.equals(query) && !lastQuery.equals("")) {
            return;
        }
        lastQuery = query;
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
        if (venue != null) {
            builder.setVenueId(venue.getId());
        }
        if (universe != null) {
            builder.setUniverseId(universe.getId());
        }
        SearchParams params = builder.build();
        fragment.showSearchDirectionLoading();
        api.search(params, new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                // Display the result
                new Handler(Looper.getMainLooper()).post(() -> {
                    fragment.showSearchResults(mapwizeObjects);
                    fragment.hideSearchDirectionLoading();
                });
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
            }
        });
    }

    @Override
    public void onDirectionToQueryChange(String query) {
        if (lastQuery != null && lastQuery.equals(query) && !lastQuery.equals("")) {
            return;
        }
        lastQuery = query;
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
        if (venue != null) {
            builder.setVenueId(venue.getId());
        }
        if (universe != null) {
            builder.setUniverseId(universe.getId());
        }
        SearchParams params = builder.build();
        fragment.showSearchDirectionLoading();
        api.search(params, new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                // Display the result
                new Handler(Looper.getMainLooper()).post(() -> {
                    fragment.showSearchResults(mapwizeObjects);
                    fragment.hideSearchDirectionLoading();
                });
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
            }
        });
    }

    private void startDirection() {
        if (mapwizeMap == null) {
            return;
        }
        setState(UIState.DIRECTION);
        fragment.hideLanguagesSelector();
        fragment.hideUniversesSelector();
        fragment.hideSearchResultsList();

        MarkerOptions.Builder startMarkerOptionsBuilder = new MarkerOptions.Builder()
                .iconName(DEFAULT_DIRECTION_START_MARKER_NAME);
        MarkerOptions.Builder endMarkerOptionsBuilder = new MarkerOptions.Builder()
                .iconName(DEFAULT_DIRECTION_END_MARKER_NAME);

        if (from instanceof Place) {
            startMarkerOptionsBuilder.title(((Place) from).getTranslation(language).getTitle());
        } else if (from instanceof PlacePreview) {
            startMarkerOptionsBuilder.title(((PlacePreview) from).getTitle());
        } else if (from instanceof Placelist) {
            startMarkerOptionsBuilder.title(((Placelist) from).getTranslation(language).getTitle());
        }
        MarkerOptions startMarkerOptions = startMarkerOptionsBuilder.build();

        if (to instanceof Place) {
            endMarkerOptionsBuilder.title(((Place) to).getTranslation(language).getTitle());
        } else if (to instanceof PlacePreview) {
            endMarkerOptionsBuilder.title(((PlacePreview) to).getTitle());
        } else if (to instanceof Placelist) {
            endMarkerOptionsBuilder.title(((Placelist) to).getTranslation(language).getTitle());
        }
        MarkerOptions endMarkerOptions = endMarkerOptionsBuilder.build();

        if (from instanceof MapwizeIndoorLocation) {
            fragment.showDirectionLoading();
            try {
                mapwizeMap.startNavigation(to, directionMode, new DirectionOptions.Builder()
                        .startMarkerOptions(startMarkerOptions)
                        .endMarkerOptions(endMarkerOptions)
                        .build(), new OnNavigationUpdateListener() {
                    @Override
                    public boolean shouldRecomputeNavigation(@NonNull NavigationInfo navigationInfo) {
                        fragment.showNavigationInfo(navigationInfo);
                        if (navigationInfo.getLocationDelta() > 15) {
                            fragment.showDirectionLoading();
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
                            fragment.showDirectionInfo(mapwizeMap.getDirection());
                            fragment.showSwapButton();
                            EventManager.getInstance().triggerOnDirectionStart(venue, universe, from, to, directionMode.getId(), true);
                        });
                    }

                    @Override
                    public void navigationDidFail(Throwable throwable) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            fragment.showDirectionError();
                            fragment.showSwapButton();
                            mapwizeMap.removeMarkers();
                            mapwizeMap.removeDirection();
                            mapwizeMap.stopNavigation();
                        });
                    }
                });
            } catch (NavigationException e) {
                e.printStackTrace();
            }
        } else {
            fragment.showDirectionLoading();
            MarkerOptions finalStartMarkerOptions = startMarkerOptions;
            MarkerOptions finalEndMarkerOptions = endMarkerOptions;
            api.getDirection(from, to, directionMode, new ApiCallback<Direction>() {
                @Override
                public void onSuccess(@NonNull Direction direction) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mapwizeMap.removeMarkers();
                        mapwizeMap.unselectPlace();
                        mapwizeMap.setDirection(direction, new DirectionOptions.Builder()
                                .startMarkerOptions(finalStartMarkerOptions)
                                .endMarkerOptions(finalEndMarkerOptions)
                                .build());
                        new Handler(Looper.getMainLooper()).post(() -> {
                            fragment.showDirectionInfo(direction);
                            fragment.showSwapButton();
                        });
                        EventManager.getInstance().triggerOnDirectionStart(venue, universe, from, to, directionMode.getId(), false);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        fragment.showDirectionError();
                        fragment.showSwapButton();
                        mapwizeMap.removeMarkers();
                        mapwizeMap.removeDirection();
                    });
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.venue = savedInstanceState.getParcelable(venue_KEY);
        this.universes = savedInstanceState.getParcelableArrayList(universes_KEY);
        this.universe = savedInstanceState.getParcelable(universe_KEY);
        this.state = (UIState) savedInstanceState.getSerializable(state_KEY);

        this.lastQuery = (String) savedInstanceState.getSerializable(lastQuery_KEY);

        this.directionMode = savedInstanceState.getParcelable(directionMode_KEY);
        this.from = savedInstanceState.getParcelable(from_KEY);
        this.to = savedInstanceState.getParcelable(to_KEY);

        if (state == UIState.SEARCH) {
            fragment.showSearch();
        } else if (state == UIState.SEARCH_FROM) {
            fragment.showSearch();
            fragment.showDirectionSearchBar();
            fragment.hideSearchBar();
            fragment.hideInfo();
            fragment.showAccessibleDirectionModes(directionModes);
            validateDirectionMode(directionMode);
            fragment.hideSwapButton();
            fragment.showSearchDirectionFrom();
        } else if (state == UIState.SEARCH_TO) {
            fragment.showSearch();
            fragment.showDirectionSearchBar();
            fragment.hideSearchBar();
            fragment.hideInfo();
            fragment.showAccessibleDirectionModes(directionModes);
            validateDirectionMode(directionMode);
            fragment.hideSwapButton();
            fragment.showSearchDirectionTo();
        } else if (state == UIState.DIRECTION) {
            fragment.showDirectionSearchBar();
            fragment.showAccessibleDirectionModes(directionModes);
            validateDirectionMode(directionMode);
            fragment.showSwapButton();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putParcelable(venue_KEY, venue);
        saveInstanceState.putParcelableArrayList(universes_KEY, (ArrayList) universes);
        saveInstanceState.putParcelable(universe_KEY, universe);
        saveInstanceState.putSerializable(state_KEY, state);
        saveInstanceState.putSerializable(lastQuery_KEY, lastQuery);
        saveInstanceState.putSerializable(directionMode_KEY, directionMode);
        saveDirectionPoint(saveInstanceState, from, from_KEY);
        saveDirectionPoint(saveInstanceState, to, to_KEY);
    }

    private void saveDirectionPoint(Bundle saveInstanceState, DirectionPoint directionPoint, String key) {
        if (directionPoint != null) {
            if (directionPoint instanceof Place) {
                saveInstanceState.putParcelable(key, (Place) directionPoint);
            } else if (directionPoint instanceof Placelist) {
                saveInstanceState.putParcelable(key, (Placelist) directionPoint);
            } else if (directionPoint instanceof LatLngFloorInVenue) {
                saveInstanceState.putParcelable(key, (LatLngFloorInVenue) directionPoint);
            } else if (directionPoint instanceof LatLngFloor) {
                saveInstanceState.putParcelable(key, (LatLngFloor) directionPoint);
            } else if (directionPoint instanceof MapwizeIndoorLocation) {
                saveInstanceState.putParcelable(key, (MapwizeIndoorLocation) directionPoint);
            }
        }
    }


    public void setState(UIState state) {
        this.state = state;
        fragment.invalidateOnBackPressedCallbackState();
    }
}