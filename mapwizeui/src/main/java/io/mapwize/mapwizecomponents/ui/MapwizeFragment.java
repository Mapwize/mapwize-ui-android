package io.mapwize.mapwizecomponents.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.AccountManager;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.api.Direction;
import io.mapwize.mapwizeformapbox.api.DirectionPoint;
import io.mapwize.mapwizeformapbox.api.LatLngFloor;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.PlaceList;
import io.mapwize.mapwizeformapbox.api.Universe;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.ClickEvent;
import io.mapwize.mapwizeformapbox.map.FollowUserMode;
import io.mapwize.mapwizeformapbox.map.MapOptions;
import io.mapwize.mapwizeformapbox.map.MapwizeIndoorLocation;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;
import io.mapwize.mapwizeformapbox.map.MapwizePluginFactory;
import io.mapwize.mapwizeformapbox.map.UISettings;

/**
 * Mapwize Fragment allow you to integrate Mapwize in a simplest way.
 */
public class MapwizeFragment extends Fragment implements CompassView.OnCompassClickListener,
        SearchBarView.SearchBarListener, SearchDirectionView.SearchDirectionListener,
        MapwizePlugin.OnVenueEnterListener, MapwizePlugin.OnVenueExitListener,
        BottomCardView.BottomCardListener, FollowUserButton.FollowUserButtonListener {

    private static String ARG_OPTIONS = "param_options";
    private static String ARG_UI_SETTINGS = "param_ui_settings";
    private static String ARG_MAPBOX_OPTIONS = "param_mapbox_options";

    // Component listener
    private OnFragmentInteractionListener listener;

    // Component initialization params
    private MapOptions initializeOptions = null;
    private MapwizeFragmentUISettings initializeUiSettings = null;
    private MapboxMapOptions initializeMapboxOptions = null;
    private Place initializePlace = null;

    // Component map & mapwize
    private MapboxMap mapboxMap;
    private MapwizePlugin mapwizePlugin;

    // Component views
    private ConstraintLayout mainLayout;
    private MapView mapView;
    private CompassView compassView;
    private FollowUserButton followUserButton;
    private FloorControllerView floorControllerView;
    private SearchBarView searchBarView;
    private SearchDirectionView searchDirectionView;
    private LanguagesButton languagesButton;
    private UniversesButton universesButton;
    private BottomCardView bottomCardView;
    private SearchResultList searchResultList;

    // Component state
    private MapwizeObject selectedContent;
    private boolean isInDirection = false;

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the SDK
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        MapwizeFragmentUISettings uiSettings = new MapwizeFragmentUISettings.Builder().build();
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the SDK
     * @param uiSettings used to display/hide UI elements
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the SDK
     * @param uiSettings used to display/hide UI elements
     * @param mapboxMapOptions used to pass Mapbox options at start
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        bundle.putParcelable(ARG_MAPBOX_OPTIONS, mapboxMapOptions);
        mf.setArguments(bundle);
        return mf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            initializeOptions = bundle.getParcelable(ARG_OPTIONS);
            initializeUiSettings = bundle.getParcelable(ARG_UI_SETTINGS);
            initializeMapboxOptions = bundle.getParcelable(ARG_MAPBOX_OPTIONS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), "pk.mapwize");
        return inflater.inflate(R.layout.mapwize_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapboxMapOptions mapboxMapOptions = new MapboxMapOptions();
        if (initializeMapboxOptions != null) {
            mapboxMapOptions = initializeMapboxOptions;
        }
        mapboxMapOptions.logoEnabled(false);
        mapboxMapOptions.attributionEnabled(false);
        mapboxMapOptions.compassEnabled(false);
        mapView = new MapView(view.getContext(), mapboxMapOptions);
        FrameLayout layout = view.findViewById(R.id.mapViewContainer);
        layout.addView(mapView);

        mapView.addOnDidFinishRenderingMapListener(fully -> layout.setVisibility(View.VISIBLE));

        loadViews(view);
        if (initializeUiSettings.isFloorControllerHidden()) {
            floorControllerView.setVisibility(View.GONE);
        }
        if (initializeUiSettings.isFollowUserButtonHidden()) {
            followUserButton.setVisibility(View.GONE);
        }
        if (initializeUiSettings.isCompassHidden()) {
            compassView.setVisibility(View.GONE);
        }

        // Use the custom Mapwize style instead of Mapbox style
        mapView.onCreate(savedInstanceState);

        // Hide all default sdk components as we are create them in the fragment
        UISettings settings = new UISettings.Builder(view.getContext())
                .mapwizeCompassEnabled(false)
                .showFloorControl(false)
                .showUserPositionControl(false)
                .build();

        // Set default options if needed
        if (initializeOptions == null) {
            initializeOptions = new MapOptions.Builder().build();
        }

        // Instantiate Mapwize sdk
        mapwizePlugin = MapwizePluginFactory.create(mapView, initializeOptions, settings);
        initMapwizeListeners(mapwizePlugin);

        // Call when mapbox is loaded
        mapView.getMapAsync(mMap -> {
            mapboxMap = mMap;
            mapboxMap.setStyle("https://outdoor.mapwize.io/styles/mapwize/style.json?key=" +
                    AccountManager.getInstance().getApiKey());
            // Initialize UI Components
            initCompass(compassView, initializeUiSettings);
            initFollowUserModeButton(followUserButton, initializeUiSettings);
            initFloorController(floorControllerView, initializeUiSettings, listener);
            initSearchBar(searchBarView, initializeUiSettings);
            initDirectionBar(searchDirectionView);
            initUniversesButton(universesButton);
            initLanguagesButton(languagesButton);
            initBottomCardView(bottomCardView, listener);

        });
        mainLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onInflate(@Nullable Context context, @Nullable AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (mapwizePlugin != null) {
            mapwizePlugin.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null && !mapView.isDestroyed()) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null && !mapView.isDestroyed()) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        if (mapwizePlugin != null) {
            mapwizePlugin.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapView != null) {
            mapView.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    private void loadViews(View view) {
        mainLayout = view.findViewById(R.id.mapwizeFragmentLayout);
        compassView = view.findViewById(R.id.mapwizeCompassView);
        followUserButton = view.findViewById(R.id.mapwizeFollowUserButton);
        floorControllerView = view.findViewById(R.id.mapwizeFloorController);
        searchBarView = view.findViewById(R.id.mapwizeSearchBar);
        searchDirectionView = view.findViewById(R.id.mapwizeDirectionSearchBar);
        universesButton = view.findViewById(R.id.mapwizeUniversesButton);
        languagesButton = view.findViewById(R.id.mapwizeLanguagessButton);
        bottomCardView = view.findViewById(R.id.mapwizeBottomCardView);
        searchResultList = view.findViewById(R.id.mapwizeSearchResultList);
    }

    private void initBottomCardView(BottomCardView bottomCardView, OnFragmentInteractionListener listener) {
        bottomCardView.setListener(this);
        bottomCardView.setInteractionListener(listener);
    }

    private void initFloorController(FloorControllerView floorControllerView, MapwizeFragmentUISettings uiSettings, OnFragmentInteractionListener listener) {
        if (uiSettings.isFloorControllerHidden()) {
            floorControllerView.setVisibility(View.GONE);
        }
        else {
            floorControllerView.setMapwizePlugin(mapwizePlugin);
            floorControllerView.setUiBehaviour(listener);
        }
    }

    private void initFollowUserModeButton(FollowUserButton followUserButton, MapwizeFragmentUISettings uiSettings) {
        if (uiSettings.isFollowUserButtonHidden()) {
            followUserButton.setVisibility(View.GONE);
        }
        else {
            followUserButton.setMapwizePlugin(mapwizePlugin);
            followUserButton.setListener(this);
        }
    }

    private void initCompass(CompassView compassView, MapwizeFragmentUISettings uiSettings) {
        if (!uiSettings.isCompassHidden()) {
            compassView.setMapboxMap(mapboxMap);
            compassView.fadeCompassViewFacingNorth(true);
            compassView.setOnCompassClickListener(this);
        }
    }

    private void initSearchBar(SearchBarView searchBarView, MapwizeFragmentUISettings uiSettings) {
        searchBarView.setMenuHidden(uiSettings.isMenuButtonHidden());
        searchBarView.setMapwizePlugin(mapwizePlugin);
        searchBarView.setListener(this);
        searchBarView.setResultList(searchResultList);
    }

    private void initDirectionBar(SearchDirectionView searchDirectionView) {
        searchDirectionView.setMapwizePlugin(mapwizePlugin);
        searchDirectionView.setListener(this);
        searchDirectionView.setDirectionInfoView(bottomCardView);
    }

    private void initUniversesButton(UniversesButton universesButton) {
        universesButton.setMapwizePlugin(mapwizePlugin);
    }

    private void initLanguagesButton(LanguagesButton languagesButton) {
        languagesButton.setMapwizePlugin(mapwizePlugin);
    }

    private void initMapwizeListeners(MapwizePlugin mapwizePlugin) {
        // Configure click event
        mapwizePlugin.addOnClickListener(event -> {
            switch (event.getEventType()) {
                case ClickEvent.MAP_CLICK:
                    onMapClick(event.getLatLngFloor());
                    break;
            }
            switch (event.getEventType()) {
                case ClickEvent.PLACE_CLICK:
                    onPlaceClick(event.getPlace());
                    break;
            }
            switch (event.getEventType()) {
                case ClickEvent.VENUE_CLICK:
                    onVenueClick(event.getVenue());
                    break;
            }
        });

        // Configure enter and exit venue event
        mapwizePlugin.addOnVenueEnterListener(this);
        mapwizePlugin.addOnVenueExitListener(this);

        // Configure did load event
        mapwizePlugin.setOnDidLoadListener(mapwizePlugin1 -> {

            // If a place has been pass as parameter, set the universe to ensure that the selected
            // place belong to the displayed universe
            if (initializeOptions.getCenterOnPlaceId() != null) {
                Api.getPlace(initializeOptions.getCenterOnPlaceId(), new ApiCallback<Place>() {
                    @Override
                    public void onSuccess(@Nullable Place place) {
                        initializePlace = place;
                        Universe toUniverse = initializePlace.getUniverses().get(0);
                        if (toUniverse != null) {
                            mapwizePlugin.setUniverse(toUniverse);
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Throwable throwable) {

                    }
                });
            }
            if (this.listener != null) {
                this.listener.onFragmentReady(mapboxMap, mapwizePlugin);
            }
        });
    }

    /**
     * Method called when the user click on the map
     * @param coordinate the coordinate of the click
     */
    private void onMapClick(LatLngFloor coordinate) {
        if (!isInDirection) {
            if (selectedContent != null) {
                unselectContent();
            }
        }
        else if (mapwizePlugin.getDirection() != null) {
            if (searchDirectionView.getVisibility() == View.VISIBLE) {
                searchDirectionView.setVisibility(View.GONE);
                bottomCardView.setVisibility(View.GONE);
            }
            else {
                searchDirectionView.setVisibility(View.VISIBLE);
                bottomCardView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Method called when the user click on a place
     * @param place the clicked place
     */
    private void onPlaceClick(Place place) {
        if (!isInDirection) {
            selectPlace(place, false);
        }
    }

    /**
     * Method called when the user click on a venue
     * @param venue the clicked venue
     */
    private void onVenueClick(Venue venue) {
        if (!isInDirection) {
            selectVenue(venue);
        }
    }

    /**
     * Setup the UI to display information about the selected place
     * @param place the selected place
     * @param centerOn if true, center on the place
     */
    public void selectPlace(Place place, boolean centerOn) {
        selectedContent = place;
        bottomCardView.setContent(place, mapwizePlugin.getLanguage());
        mapwizePlugin.removeMarkers();
        mapwizePlugin.addMarker(place, marker -> {

        });
        mapwizePlugin.addPromotedPlace(place);
        if (centerOn) {
            mapwizePlugin.centerOnPlace(place);
        }
    }

    /**
     * Setup the UI to display information about the selected venue
     * @param venue the venue to select
     */
    public void selectVenue(Venue venue) {
        bottomCardView.setContent(venue, mapwizePlugin.getLanguage());
        mapwizePlugin.centerOnVenue(venue);
    }

    /**
     * Setup the UI to display information about the selected placelist
     * @param placeList the selected placelist
     */
    public void selectPlaceList(PlaceList placeList) {
        selectedContent = placeList;
        bottomCardView.setContent(placeList, mapwizePlugin.getLanguage());
        mapwizePlugin.addMarkers(placeList, markers -> {

        });
    }

    /**
     * Hide the UI component, remove markers and unpromote place if needed
     * If we are in a venue, displayed the venue information
     */
    public void unselectContent() {
        bottomCardView.removeContent();
        mapwizePlugin.removeMarkers();
        if (selectedContent instanceof Place) {
            mapwizePlugin.removePromotedPlace((Place) selectedContent);
        }
        selectedContent = null;
        if (mapwizePlugin.getVenue() != null) {
            bottomCardView.setContent(mapwizePlugin.getVenue(), mapwizePlugin.getLanguage());
        }
    }

    /**
     * Setup the UI to display direction module
     * If a place or a placelist is selected, to field will be set
     * If we have a user indoor location, from field will be set
     * If both field are set, the direction start
     */
    private void showDirectionUI() {
        isInDirection = true;
        searchBarView.setVisibility(View.GONE);
        searchDirectionView.setVisibility(View.VISIBLE);
        searchDirectionView.setResultList(searchResultList);
        if (selectedContent != null) {
            searchDirectionView.setToDirectionPoint(selectedContent);
            unselectContent();
        }
        if (mapwizePlugin.getUserPosition() != null && mapwizePlugin.getUserPosition().getFloor() != null) {
            searchDirectionView.setFromDirectionPoint(new MapwizeIndoorLocation(mapwizePlugin.getUserPosition()));
        }
        bottomCardView.removeContent();
        universesButton.hide();
    }

    /**
     * Set a direction on Mapwize UI will display the direction and the user interface
     * @param direction to display
     * @param from the starting point
     * @param to the destination point
     * @param isAccessible true if the direction is in accessible mode
     */
    public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, boolean isAccessible) {
        isInDirection = true;
        searchBarView.setVisibility(View.GONE);
        searchDirectionView.setVisibility(View.VISIBLE);
        searchDirectionView.setResultList(searchResultList);
        searchDirectionView.setAccessibility(isAccessible);
        searchDirectionView.setFromDirectionPoint(from);
        searchDirectionView.setToDirectionPoint(to);
    }

    /**
     * Setup the default UI
     */
    private void showDefaultUi() {
        isInDirection = false;
        searchBarView.setVisibility(View.VISIBLE);
        searchDirectionView.setVisibility(View.GONE);
        searchBarView.setResultList(searchResultList);
        if (mapwizePlugin.getVenue() != null) {
            bottomCardView.setContent(mapwizePlugin.getVenue(), mapwizePlugin.getLanguage());
        }
        universesButton.showIfNeeded();
    }

    /**
     * Helper method to get access and refresh the UI
     * @param accesskey
     * @param callback called when the method is ended
     */
    public void grantAccess(String accesskey, ApiCallback<Boolean> callback) {
        mapwizePlugin.grantAccess(accesskey, new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean object) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (mapwizePlugin.getVenue() != null) {
                        universesButton.refreshVenue(mapwizePlugin.getVenue());
                    }
                    callback.onSuccess(object);
                });
            }

            @Override
            public void onFailure(@Nullable Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    // Bottom view listener
    @Override
    public void onDirectionClick() {
        showDirectionUI();
    }

    @Override
    public void onInformationClick() {
        listener.onInformationButtonClick(selectedContent);
    }

    @Override
    public void onDetailsOpen() {
        searchBarView.setVisibility(View.GONE);
    }

    @Override
    public void onDetailsClose() {
        searchBarView.setVisibility(View.VISIBLE);
    }

    // Compass listener
    @Override
    public void onClick(CompassView compassView) {
        mapwizePlugin.setFollowUserMode(FollowUserMode.NONE);
    }

    // Search bar listener
    @Override
    public void onSearchResult(Place place, Universe universe) {
        if (universe != null && (mapwizePlugin.getUniverse() == null || !universe.getId().equals(mapwizePlugin.getUniverse().getId()))) {
            mapwizePlugin.setUniverse(universe);
        }
        selectPlace(place, true);
    }

    @Override
    public void onSearchResult(PlaceList placeList) {
        selectPlaceList(placeList);
    }

    @Override
    public void onSearchResult(Venue venue) {
        selectVenue(venue);
    }

    @Override
    public void onLeftButtonClick(View view) {
        listener.onMenuButtonClick();
    }

    @Override
    public void onRightButtonClick(View view) {
        showDirectionUI();
    }

    // Direction bar listener
    @Override
    public void onBackClick() {
        showDefaultUi();
    }

    // Mapwize listener
    @Override
    public void onVenueExit(@NonNull Venue venue) {
        if (mapwizePlugin.getDirection() == null) {
            unselectContent();
        }
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        bottomCardView.setContent(venue, mapwizePlugin.getLanguage());
        if (initializePlace != null) {
            selectPlace(initializePlace, false);
            initializePlace = null;
        }
    }

    @Override
    public void willEnterInVenue(@NonNull Venue venue) {

    }

    @Override
    public void onFollowUserClickWithoutLocation() {
        listener.onFollowUserButtonClickWithoutLocation();
    }

    /**
     * The activity that embed this fragment must implement this interface
     */
    public interface OnFragmentInteractionListener {
        void onMenuButtonClick();
        void onInformationButtonClick(MapwizeObject mapwizeObject);
        void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin);
        void onFollowUserButtonClickWithoutLocation();
        default boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
            return true;
        }
        default boolean shouldDisplayFloorController(List<Double> floors) {
            return true;
        }
    }
}
