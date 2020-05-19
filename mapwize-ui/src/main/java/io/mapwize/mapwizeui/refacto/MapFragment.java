package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

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
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizeui.BottomCardView;
import io.mapwize.mapwizeui.CompassView;
import io.mapwize.mapwizeui.FloorControllerView;
import io.mapwize.mapwizeui.FollowUserButton;
import io.mapwize.mapwizeui.LanguagesButton;
import io.mapwize.mapwizeui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeui.R;
import io.mapwize.mapwizeui.SearchBarView;
import io.mapwize.mapwizeui.SearchDirectionView;
import io.mapwize.mapwizeui.SearchResultList;
import io.mapwize.mapwizeui.UniversesButton;

public class MapFragment extends Fragment implements BaseFragment, SearchBarView.SearchBarListener,
        SearchResultList.SearchResultListListener, FloorControllerView.OnFloorClickListener,
        BottomCardView.BottomCardListener, SearchDirectionView.SearchDirectionListener {

    // Options
    private static String ARG_OPTIONS = "param_options";
    private static String ARG_UI_SETTINGS = "param_ui_settings";
    private static String ARG_MAPWIZE_CONFIGURATION = "param_mapwize_configuration";

    // Component initialization params
    private MapOptions initializeOptions = null;
    private MapwizeFragmentUISettings initializeUiSettings = null;
    private Place initializePlace = null;

    // Component map & mapwize
    private MapwizeMap mapwizeMap;
    private MapwizeView mapwizeView;
    private MapwizeConfiguration mapwizeConfiguration;

    private BasePresenter presenter;

    private BottomCardView bottomCardView;
    private FloorControllerView floorControllerView;
    private UniversesButton universesButton;
    private LanguagesButton languagesButton;
    private SearchBarView searchBarView;
    private SearchResultList searchResultList;
    private SearchDirectionView searchDirectionView;
    private FollowUserButton followUserButton;
    private CompassView compassView;

    // Component listener
    private MapFragment.OnFragmentInteractionListener listener;

    /**
     * Create a instance of MapwizeFragment
     * @param mapOptions used to setup the map
     * @return a new instance of MapwizeFragment
     */
    public static MapFragment newInstance(@NonNull MapOptions mapOptions) {
        MapFragment mf = new MapFragment();
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
    public static MapFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions) {
        MapFragment mf = new MapFragment();
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
    public static MapFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings) {
        MapFragment mf = new MapFragment();
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
    public static MapFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings) {
        MapFragment mf = new MapFragment();
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
    public static MapFragment newInstance(@NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions) {
        MapFragment mf = new MapFragment();
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
    public static MapFragment newInstance(@NonNull MapwizeConfiguration mapwizeConfiguration, @NonNull MapOptions mapOptions, @NonNull MapwizeFragmentUISettings uiSettings, @NonNull MapboxMapOptions mapboxMapOptions) {
        MapFragment mf = new MapFragment();
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
        if (initializeOptions == null) {
            initializeOptions = new MapOptions.Builder().build();
        }
        if (initializeUiSettings == null) {
            initializeUiSettings = new MapwizeFragmentUISettings.Builder().build();
        }
        if (mapwizeConfiguration == null) {
            mapwizeConfiguration = MapwizeConfiguration.getInstance();
        }
        presenter = new MapPresenter(this, mapwizeConfiguration, initializeOptions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(container.getContext(), "pk.mapwize");
        return inflater.inflate(R.layout.mapwize_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapwizeView = new MapwizeView(view.getContext(), mapwizeConfiguration, initializeOptions);
        FrameLayout layout = view.findViewById(R.id.mapViewContainer);
        layout.addView(mapwizeView);
        mapwizeView.onCreate(savedInstanceState);

        // Instantiate Mapwize sdk
        mapwizeView.getMapAsync(mMap -> {
            mapwizeMap = mMap;
            mapwizeMap.getMapboxMap().getUiSettings().setCompassEnabled(false);
            presenter.onMapLoaded(mapwizeMap);
        });

        bottomCardView = view.findViewById(R.id.mapwizeBottomCardView);
        bottomCardView.setListener(this);
        floorControllerView = view.findViewById(R.id.mapwizeFloorController);
        floorControllerView.setListener(this);
        universesButton = view.findViewById(R.id.mapwizeUniversesButton);
        languagesButton = view.findViewById(R.id.mapwizeLanguagessButton);
        searchBarView = view.findViewById(R.id.mapwizeSearchBar);
        searchBarView.setListener(this);
        searchResultList = view.findViewById(R.id.mapwizeSearchResultList);
        searchResultList.setListener(this);
        searchDirectionView = view.findViewById(R.id.mapwizeDirectionSearchBar);
        searchDirectionView.setListener(this);
        followUserButton = view.findViewById(R.id.mapwizeFollowUserButton);
        compassView = view.findViewById(R.id.mapwizeCompassView);
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



    // Scene management
    public void showDefaultScene() {
        searchBarView.showOutOfVenue();
        searchBarView.setVisibility(View.VISIBLE);
        universesButton.setVisibility(View.GONE);
        languagesButton.setVisibility(View.GONE);
        searchDirectionView.setVisibility(View.GONE);
    }

    public void showVenueEntering(Venue venue, String language) {
        searchBarView.showVenueEntering(venue, language);
    }

    @Override
    public void showPlacePreviewInfo(PlacePreview preview, String language) {
        bottomCardView.setContent(preview);
    }

    @Override
    public void showPlaceInfoFromPreview(Place place, String language) {
        bottomCardView.setContentFromPreview(place, language);
    }

    @Override
    public void showPlaceInfo(Place place, String language) {
        bottomCardView.setContent(place, language);
    }

    @Override
    public void showPlacelistInfo(Placelist placelist, String language) {
        bottomCardView.setContent(placelist, language);
    }

    @Override
    public void hidePlaceInfo() {
        bottomCardView.removeContent();
    }

    @Override
    public void showSearchScene() {
        searchBarView.setupInSearch();
        searchResultList.show();
    }

    @Override
    public void hideSearchScene() {
        searchBarView.setupDefault();
        searchResultList.hide();
    }

    @Override
    public void showSearchDirectionScene() {
        searchBarView.setVisibility(View.GONE);
        searchDirectionView.setVisibility(View.VISIBLE);
        searchResultList.show();
    }

    @Override
    public void showDirectionLoadingScene() {
        universesButton.setVisibility(View.GONE);
        languagesButton.setVisibility(View.GONE);
        searchResultList.hide();
        searchDirectionView.showSwapButton();
    }

    @Override
    public void showDirectionScene() {

    }

    @Override
    public void hideSearchDirectionScene() {
        searchBarView.setVisibility(View.VISIBLE);
        searchDirectionView.setVisibility(View.GONE);
        universesButton.showIfNeeded();
        languagesButton.showIfNeeded();
        searchResultList.hide();
        showFromDirection(null, null);
        showToDirection(null, null);
    }

    @Override
    public void showFromDirection(DirectionPoint from, String language) {
        searchDirectionView.setFromTitle(from, language);
    }

    @Override
    public void showToDirection(DirectionPoint to, String language) {
        searchDirectionView.setToTitle(to, language);
    }

    @Override
    public void showDirectionModes(List<DirectionMode> modes) {
        searchDirectionView.setModes(modes);
    }

    @Override
    public void showDirectionMode(DirectionMode mode) {
        searchDirectionView.setMode(mode);
    }

    @Override
    public void openSearchDirectionFrom() {
        searchResultList.show();
        searchDirectionView.openFromSearch();
    }

    @Override
    public void openSearchDirectionTo() {
        searchResultList.show();
        searchDirectionView.openToSearch();
    }

    @Override
    public void hideSearchList() {
        searchResultList.hide();
    }

    @Override
    public void showDirection(Direction direction) {
        searchResultList.hide();
    }

    @Override
    public void showLanguageButton(List<String> languages) {
        languagesButton.setLanguages(languages);
        languagesButton.setListener(language -> presenter.onLanguageClick(language));
    }

    @Override
    public void showUniverseButton(List<Universe> universes) {
        universesButton.setUniverses(universes);
        universesButton.setListener(universe -> presenter.onUniverseClick(universe));
    }

    public void showVenueEntered(Venue venue, String language) {
        searchBarView.showVenueEntered(venue, language);
    }

    public void showActiveFloors(List<Floor> floors) {
        floorControllerView.setFloors(floors);
    }

    @Override
    public void showLoadingFloor(Floor floor) {
        floorControllerView.setLoadingFloor(floor);
    }

    public void showActiveFloor(Floor floor) {
        floorControllerView.setFloor(floor);
    }

    @Override
    public void showDirectionButton() {
        searchBarView.setDirectionButtonHidden(false);
    }

    @Override
    public void hideDirectionButton() {
        searchBarView.setDirectionButtonHidden(true);
    }

    @Override
    public void showSearchResults(List<? extends MapwizeObject> results) {
        searchResultList.showData(results);
    }

    @Override
    public void showSearchResults(List<? extends MapwizeObject> results, List<Universe> universes, Universe universe) {
        searchResultList.showData(results, universes, universe);
    }

    @Override
    public void showErrorMessage(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getContext(), "Error to display", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSearchStart() {
        presenter.onQueryClick();
    }

    @Override
    public void onSearchBarMenuClick() {
        listener.onMenuButtonClick();
    }

    @Override
    public void onSearchBarQueryChange(String query) {
        presenter.onSearchQueryChange(query);
    }

    @Override
    public void onSearchBarDirectionButtonClick() {
        presenter.onDirectionButtonClick();
    }

    @Override
    public void onSearchBarBackButtonClick() {
        presenter.onSearchBackButtonClick();
    }

    @Override
    public void onSearchResultNull() {

    }

    @Override
    public void onSearchResult(Place place, Universe universe) {
        presenter.onSearchResultPlaceClick(place, universe);
    }

    @Override
    public void onSearchResult(Placelist placelist) {
        presenter.onSearchResultPlacelistClick(placelist);
    }

    @Override
    public void onSearchResult(Venue venue) {
        presenter.onSearchResultVenueClick(venue);
    }

    @Override
    public void onFloorClick(Floor floor) {
        presenter.onFloorClick(floor);
    }

    @Override
    public void onDirectionClick() {
        presenter.onDirectionButtonClick();
    }

    @Override
    public void onInformationClick() {

    }

    @Override
    public void onDetailsOpen() {

    }

    @Override
    public void onDetailsClose() {

    }

    @Override
    public void onDirectionBackClick() {
        presenter.onDirectionBackClick();
    }

    @Override
    public void onDirectionSwapClick() {
        presenter.onDirectionSwapClick();
    }

    @Override
    public void onDirectionFromQueryChange(String query) {
        presenter.onDirectionFromQueryChange(query);
    }

    @Override
    public void onDirectionToQueryChange(String query) {
        presenter.onDirectionToQueryChange(query);
    }

    @Override
    public void onDirectionModeChange(DirectionMode mode) {
        presenter.onDirectionModeChange(mode);
    }

    @Override
    public void onDirectionFromFieldGetFocus() {
        presenter.onDirectionFromFieldGetFocus();
    }

    @Override
    public void onDirectionToFieldGetFocus() {
        presenter.onDirectionToFieldGetFocus();
    }


    public interface OnFragmentInteractionListener {
        void onMenuButtonClick();
        default void onInformationButtonClick(MapwizeObject mapwizeObject) {

        }
        default void onFragmentReady(MapwizeMap mapwizeMap) {

        }
        default void onFollowUserButtonClickWithoutLocation() {

        }
        default boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
            return false;
        }
        default boolean shouldDisplayFloorController(List<Floor> floors) {
            return true;
        }
    }
}
