package io.mapwize.mapwizeui;

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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.List;

import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.LatLngFloor;
import io.mapwize.mapwizesdk.api.MapwizeApiFactory;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizesdk.map.PreviewCallback;
import io.mapwize.mapwizesdk.map.VenuePreview;

/**
 * Mapwize Fragment allow you to integrate Mapwize in a simplest way.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MapwizeFragment extends Fragment implements CompassView.OnCompassClickListener,
        SearchBarView.SearchBarListener, SearchDirectionView.SearchDirectionListener,
        MapwizeMap.OnVenueEnterListener, MapwizeMap.OnVenueExitListener,
        BottomCardView.BottomCardListener, FollowUserButton.FollowUserButtonListener {

    private static String ARG_OPTIONS = "param_options";
    private static String ARG_UI_SETTINGS = "param_ui_settings";
    private static String ARG_MAPWIZE_CONFIGURATION = "param_mapwize_configuration";

    // Component listener
    private OnFragmentInteractionListener listener;

    // Component initialization params
    private MapOptions initializeOptions = null;
    private MapwizeFragmentUISettings initializeUiSettings = null;
    private Place initializePlace = null;

    // Component map & mapwize
    private MapwizeMap mapwizeMap;
    private MapwizeView mapwizeView;
    private MapwizeConfiguration mapwizeConfiguration;

    // Component views
    private ConstraintLayout mainLayout;
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
     * @param mapOptions used to setup the map
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        MapwizeFragmentUISettings uiSettings = new MapwizeFragmentUISettings.Builder().build();
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        MapwizeConfiguration mapwizeConfiguration = MapwizeConfiguration.getInstance();
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        MapwizeFragmentUISettings uiSettings = new MapwizeFragmentUISettings.Builder().build();
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        MapwizeConfiguration mapwizeConfiguration = MapwizeConfiguration.getInstance();
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @param mapboxMapOptions used to pass Mapbox options at start
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        MapwizeConfiguration mapwizeConfiguration = MapwizeConfiguration.getInstance();
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
        mf.setArguments(bundle);
        return mf;
    }

    /**
     * Create a instance of MapwizeFragment
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @param mapboxMapOptions used to pass Mapbox options at start
     * @return a new instance of MapwizeFragment
     */
    public static MapwizeFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions) {
        MapwizeFragment mf = new MapwizeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_OPTIONS, mapOptions);
        bundle.putParcelable(ARG_UI_SETTINGS, uiSettings);
        bundle.putParcelable(ARG_MAPWIZE_CONFIGURATION, mapwizeConfiguration);
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
            mapwizeConfiguration = bundle.getParcelable(ARG_MAPWIZE_CONFIGURATION);
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

        if (initializeOptions == null) {
            initializeOptions = new MapOptions.Builder().build();
        }
        mapwizeView = new MapwizeView(getContext(), mapwizeConfiguration, initializeOptions);

        FrameLayout layout = view.findViewById(R.id.mapViewContainer);
        layout.addView(mapwizeView);
        mapwizeView.onCreate(savedInstanceState);
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

        // Instantiate Mapwize sdk
        mapwizeView.getMapAsync(mMap -> {
            mapwizeMap = mMap;
            mapwizeMap.getMapboxMap().getUiSettings().setCompassEnabled(false);
            initMapwizeListeners(mapwizeMap);
            initCompass(compassView, initializeUiSettings);
            initFollowUserModeButton(followUserButton, initializeUiSettings);
            initFloorController(floorControllerView, initializeUiSettings, listener);
            initSearchBar(searchBarView, initializeUiSettings);
            initDirectionBar(searchDirectionView);
            initUniversesButton(universesButton);
            initLanguagesButton(languagesButton);
            initBottomCardView(bottomCardView, listener);
            listener.onFragmentReady(mapwizeMap);
            if (mapwizeMap.getMapOptions().getCenterOnPlaceId() != null) {
                MapwizeApiFactory.getApi().getPlace(mapwizeMap.getMapOptions().getCenterOnPlaceId(), new ApiCallback<Place>() {
                    @Override
                    public void onSuccess(@NonNull Place place) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            selectPlace(place, false);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Throwable throwable) {

                    }
                });
            }
        });

        mainLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onInflate(@Nullable Context context, @Nullable AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
        if (mapwizeView != null) {
            mapwizeView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapwizeView != null) {
            mapwizeView.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapwizeView != null) {
            mapwizeView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapwizeView != null) {
            mapwizeView.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        if (mapwizeView != null) {
            mapwizeView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapwizeView != null) {
            mapwizeView.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapwizeView != null) {
            mapwizeView.onDestroy();
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
            floorControllerView.setMapwizeMap(mapwizeMap);
            floorControllerView.setUiBehaviour(listener);
        }
    }

    private void initFollowUserModeButton(FollowUserButton followUserButton, MapwizeFragmentUISettings uiSettings) {
        if (uiSettings.isFollowUserButtonHidden()) {
            followUserButton.setVisibility(View.GONE);
        }
        else {
            followUserButton.setMapwizeMap(mapwizeMap);
            followUserButton.setListener(this);
        }
    }

    private void initCompass(CompassView compassView, MapwizeFragmentUISettings uiSettings) {
        if (!uiSettings.isCompassHidden()) {
            compassView.setMapboxMap(mapwizeMap.getMapboxMap());
            compassView.fadeCompassViewFacingNorth(true);
            compassView.setOnCompassClickListener(this);
        }
    }

    private void initSearchBar(SearchBarView searchBarView, MapwizeFragmentUISettings uiSettings) {
        searchBarView.setMenuHidden(uiSettings.isMenuButtonHidden());
        searchBarView.setMapwizeMap(mapwizeMap);
        searchBarView.setListener(this);
        searchBarView.setResultList(searchResultList);
        searchBarView.setVisibility(View.VISIBLE);
    }

    private void initDirectionBar(SearchDirectionView searchDirectionView) {
        searchDirectionView.setMapwizeMap(mapwizeMap);
        searchDirectionView.setListener(this);
        searchDirectionView.setDirectionInfoView(bottomCardView);
    }

    private void initUniversesButton(UniversesButton universesButton) {
        universesButton.setMapwizeMap(mapwizeMap);
    }

    private void initLanguagesButton(LanguagesButton languagesButton) {
        languagesButton.setMapwizeMap(mapwizeMap);
    }

    private void initMapwizeListeners(MapwizeMap mapwizeMap) {
        mapwizeMap.addOnClickListener(event -> {
            switch (event.getEventType()) {
                case ClickEvent.MAP_CLICK:
                    onMapClick(event.getLatLngFloor());
                    break;
                case ClickEvent.PLACE_CLICK:
                    onPlaceClick(event.getPlacePreview());
                    break;
                case ClickEvent.VENUE_CLICK:
                    onVenueClick(event.getVenuePreview());
                    break;
            }
        });
        mapwizeMap.addOnVenueEnterListener(this);
        mapwizeMap.addOnVenueExitListener(this);
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
    }

    /**
     * Method called when the user click on a place
     * @param placePreview the clicked place
     */
    private void onPlaceClick(PlacePreview placePreview) {
        if (!isInDirection) {
            selectPlacePreview(placePreview, false);
        }
    }

    /**
     * Method called when the user click on a venue
     * @param venuePreview the clicked venue
     */
    private void onVenueClick(VenuePreview venuePreview) {
        if (!isInDirection) {
            selectVenuePreview(venuePreview);
        }
    }

    /**
     * Setup the UI to display information about the selected place
     * @param placePreview the selected place
     * @param centerOn if true, center on the place
     */
    public void selectPlacePreview(PlacePreview placePreview, boolean centerOn) {
        mapwizeMap.removeMarkers();
        mapwizeMap.addMarker(placePreview);
        if (centerOn) {
            mapwizeMap.centerOnPlace(placePreview, 300);
        }
        placePreview.getFullObjectAsync(new PreviewCallback<Place>() {
            @Override
            public void getObjectAsync(Place place) {
                selectedContent = place;
                bottomCardView.setContent(place, mapwizeMap.getLanguage());
                mapwizeMap.addPromotedPlace(place);
            }

            @Override
            public void error(Throwable throwable) {
                // TODO Handle error
            }
        });
    }

    /**
     * Setup the UI to display information about the selected place
     * @param place the selected place
     * @param centerOn if true, center on the place
     */
    public void selectPlace(Place place, boolean centerOn) {
        mapwizeMap.removeMarkers();
        mapwizeMap.addMarker(place);
        if (centerOn) {
            mapwizeMap.centerOnPlace(place, 300);
        }
        selectedContent = place;
        bottomCardView.setContent(place, mapwizeMap.getLanguage());
        mapwizeMap.addPromotedPlace(place);
    }

    /**
     * Setup the UI to display information about the selected venue
     * @param venuePreview the venue to select
     */
    public void selectVenuePreview(VenuePreview venuePreview) {
        mapwizeMap.centerOnVenue(venuePreview, 300);
    }

    /**
     * Setup the UI to display information about the selected venue
     * @param venue the venue to select
     */
    public void selectVenue(Venue venue) {
        mapwizeMap.centerOnVenue(venue, 300);
    }

    /**
     * Setup the UI to display information about the selected placelist
     * @param placelist the selected placelist
     */
    public void selectPlacelist(Placelist placelist) {
        selectedContent = placelist;
        bottomCardView.setContent(placelist, mapwizeMap.getLanguage());
        mapwizeMap.addMarkers(placelist, markers -> {

        });
    }

    /**
     * Hide the UI component, remove markers and unpromote place if needed
     * If we are in a venue, displayed the venue information
     */
    public void unselectContent() {
        bottomCardView.removeContent();
        mapwizeMap.removeMarkers();
        mapwizeMap.removePromotedPlaces();
        /*if (selectedContent instanceof Place) {
            mapwizeMap.removePromotedPlace((Place) selectedContent);
        }*/
        selectedContent = null;
        if (mapwizeMap.getVenue() != null) {
            bottomCardView.setContent(mapwizeMap.getVenue(), mapwizeMap.getLanguage());
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
        boolean showFrom = false;
        boolean showTo = false;
        if (selectedContent != null) {
            searchDirectionView.setToDirectionPoint(selectedContent);
            unselectContent();
        }
        else {
            showTo = true;
        }
        if (mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null) {
            searchDirectionView.setFromDirectionPoint(new MapwizeIndoorLocation(mapwizeMap.getUserLocation()));
        }
        else {
            showFrom = true;
        }
        if (showFrom) {
            searchDirectionView.fromEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
        else if (showTo) {
            searchDirectionView.toEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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
        searchDirectionView.setToDirectionPoint(to);
        searchDirectionView.setFromDirectionPoint(from);
    }

    /**
     * Setup the default UI
     */
    private void showDefaultUi() {
        isInDirection = false;
        searchBarView.setVisibility(View.VISIBLE);
        searchDirectionView.setVisibility(View.GONE);
        searchBarView.setResultList(searchResultList);
        if (mapwizeMap.getVenue() != null) {
            bottomCardView.setContent(mapwizeMap.getVenue(), mapwizeMap.getLanguage());
        }
        universesButton.showIfNeeded();
    }

    /**
     * Helper method to get access and refresh the UI
     * @param accesskey
     * @param callback called when the method is ended
     */
    public void grantAccess(String accesskey, ApiCallback<Boolean> callback) {
        mapwizeMap.grantAccess(accesskey, new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean object) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (mapwizeMap.getVenue() != null) {
                        universesButton.refreshVenue(mapwizeMap.getVenue());
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
        mapwizeMap.setFollowUserMode(FollowUserMode.NONE);
    }

    // Search bar listener
    @Override
    public void onSearchResult(Place place, Universe universe) {
        if (universe != null && (mapwizeMap.getUniverse() == null || !universe.getId().equals(mapwizeMap.getUniverse().getId()))) {
            mapwizeMap.setUniverse(universe);
        }
        selectPlace(place, true);
    }

    @Override
    public void onSearchResult(Placelist placelist) {
        selectPlacelist(placelist);
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
        if (mapwizeMap.getDirection() == null) {
            unselectContent();
        }
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        bottomCardView.setContent(venue, mapwizeMap.getLanguage());
        if (initializePlace != null) {
            selectPlace(initializePlace, false);
            initializePlace = null;
        }
    }

    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {

    }

    @Override
    public void onFollowUserClickWithoutLocation() {
        listener.onFollowUserButtonClickWithoutLocation();
    }

    /**
     * Getter for UI Component
     */
    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }

    public CompassView getCompassView() {
        return compassView;
    }

    public FollowUserButton getFollowUserButton() {
        return followUserButton;
    }

    public FloorControllerView getFloorControllerView() {
        return floorControllerView;
    }

    public SearchBarView getSearchBarView() {
        return searchBarView;
    }

    public SearchDirectionView getSearchDirectionView() {
        return searchDirectionView;
    }

    public LanguagesButton getLanguagesButton() {
        return languagesButton;
    }

    public UniversesButton getUniversesButton() {
        return universesButton;
    }

    public BottomCardView getBottomCardView() {
        return bottomCardView;
    }

    public SearchResultList getSearchResultList() {
        return searchResultList;
    }

    /**
     * The activity that embed this fragment must implement this interface
     */
    public interface OnFragmentInteractionListener {
        void onMenuButtonClick();
        void onInformationButtonClick(MapwizeObject mapwizeObject);
        void onFragmentReady(MapwizeMap mapwizeMap);
        void onFollowUserButtonClickWithoutLocation();
        default boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
            return true;
        }
        default boolean shouldDisplayFloorController(List<Floor> floors) {
            return true;
        }
    }
}
