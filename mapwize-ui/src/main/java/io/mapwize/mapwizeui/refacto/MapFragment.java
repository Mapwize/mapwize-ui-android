package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.ClickEvent;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizeui.CompassView;
import io.mapwize.mapwizeui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeui.R;

public class MapFragment extends Fragment implements BaseFragment, MapwizeMap.OnVenueEnterListener,
        MapwizeMap.OnVenueExitListener, MapwizeMap.OnUniverseChangeListener, MapwizeMap.OnFloorChangeListener,
        MapwizeMap.OnFloorsChangeListener, MapwizeMap.OnDirectionModesChangeListener, MapwizeMap.OnLanguageChangeListener,
        MapwizeMap.OnFollowUserModeChangeListener, MapwizeMap.OnClickListener {

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

    private ViewGroup sceneRoot;
    private Scene currentScene;

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
        else {
            initializeOptions = new MapOptions.Builder().build();
            initializeUiSettings = new MapwizeFragmentUISettings.Builder().build();
            mapwizeConfiguration = MapwizeConfiguration.getInstance();
        }
        presenter = new MapPresenter(this, mapwizeConfiguration);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(container.getContext(), "pk.mapwize");
        return inflater.inflate(R.layout.mwz_map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sceneRoot = view.findViewById(R.id.scene_root);
        mapwizeView = new MapwizeView(view.getContext(), mapwizeConfiguration, initializeOptions);
        FrameLayout layout = view.findViewById(R.id.mapViewContainer);
        layout.addView(mapwizeView);
        mapwizeView.onCreate(savedInstanceState);

        // Instantiate Mapwize sdk
        mapwizeView.getMapAsync(mMap -> {
            mapwizeMap = mMap;
            mapwizeMap.getMapboxMap().getUiSettings().setCompassEnabled(false);
            mapwizeMap.addOnClickListener(this);
            mapwizeMap.addOnVenueEnterListener(this);
            mapwizeMap.addOnVenueExitListener(this);
            mapwizeMap.addOnDirectionModesChangeListener(this);
            mapwizeMap.addOnUniverseChangeListener(this);
            mapwizeMap.addOnLanguageChangeListener(this);
            mapwizeMap.addOnFollowUserModeChangeListener(this);
            mapwizeMap.addOnFloorChangeListener(this);
            presenter.onMapLoaded();
        });
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
        currentScene = Scene.getSceneForLayout(sceneRoot, R.layout.mwz_map_scene_default, getContext());
        TransitionManager.go(currentScene);
        SearchBarPlaceholder searchBarPlaceholder = currentScene.getSceneRoot().findViewById(R.id.mwz_search_bar_placeholder);
        searchBarPlaceholder.setText(getResources().getString(R.string.search_venue));
        searchBarPlaceholder.setDirectionButtonVisible(false);
        searchBarPlaceholder.setMenuButtonVisible(!initializeUiSettings.isMenuButtonHidden());
        searchBarPlaceholder.setListener(new SearchBarPlaceholder.Listener() {
            @Override
            public void onMenuButtonClick() {
                listener.onMenuButtonClick();
            }

            @Override
            public void onDirectionButtonClick() {
                presenter.onDirectionButtonClick();
            }
        });
        CompassView compassView = currentScene.getSceneRoot().findViewById(R.id.mwz_compass_view);
        compassView.setMapboxMap(mapwizeMap.getMapboxMap());
    }

    public void showInVenueScene(Venue venue, String language) {
        currentScene = Scene.getSceneForLayout(sceneRoot, R.layout.mwz_map_scene_in_venue, getContext());
        Fade fade = new Fade();
        fade.excludeTarget(R.id.mwz_search_bar_placeholder, true);
        fade.excludeTarget(R.id.mwz_follow_user_button, true);
        fade.excludeTarget(R.id.mwz_compass_view, true);
        TransitionManager.go(currentScene, fade);
        SearchBarPlaceholder searchBarPlaceholder = currentScene.getSceneRoot().findViewById(R.id.mwz_search_bar_placeholder);
        String searchPlaceHolder = getResources().getString(R.string.search_in_placeholder);
        searchBarPlaceholder.setText(String.format(searchPlaceHolder, venue.getTranslation(language).getTitle()));
        searchBarPlaceholder.setDirectionButtonVisible(true);
        searchBarPlaceholder.setMenuButtonVisible(!initializeUiSettings.isMenuButtonHidden());
        searchBarPlaceholder.setListener(new SearchBarPlaceholder.Listener() {
            @Override
            public void onMenuButtonClick() {
                listener.onMenuButtonClick();
            }

            @Override
            public void onDirectionButtonClick() {
                presenter.onDirectionButtonClick();
            }
        });
        CompassView compassView = currentScene.getSceneRoot().findViewById(R.id.mwz_compass_view);
        compassView.setMapboxMap(mapwizeMap.getMapboxMap());
    }

    public void showVenueEntering(Venue venue, String language) {
        ProgressBar progressBar = currentScene.getSceneRoot().findViewById(R.id.mwz_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        SearchBarPlaceholder searchBarPlaceholder = currentScene.getSceneRoot().findViewById(R.id.mwz_search_bar_placeholder);
        String searchPlaceHolder = getResources().getString(R.string.loading_venue_placeholder);
        searchBarPlaceholder.setText(String.format(searchPlaceHolder, venue.getTranslation(language).getTitle()));
    }

    // Map Listeners
    @Override
    public void onDirectionModesChange(@NonNull List<DirectionMode> directionModes) {
        presenter.onDirectionModesChange(directionModes);
    }

    @Override
    public void onFloorWillChange(@Nullable Floor floor) {
        presenter.onFloorWillChange(floor);
    }

    @Override
    public void onFloorChange(@Nullable Floor floor) {
        presenter.onFloorChange(floor);
    }

    @Override
    public void onFloorsChange(@NonNull List<Floor> floors) {
        presenter.onFloorsChange(floors);
    }

    @Override
    public void onLanguageChange(@NonNull String language) {
        presenter.onLanguageChange(language);
    }

    @Override
    public void onUniversesChange(@NonNull List<Universe> universes) {
        presenter.onUniversesChange(universes);
    }

    @Override
    public void onUniverseWillChange(@NonNull Universe universe) {
        presenter.onUniverseWillChange(universe);
    }

    @Override
    public void onUniverseChange(@Nullable Universe universe) {
        presenter.onUniverseChange(universe);
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        presenter.onVenueEnter(venue);
    }

    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {
        presenter.onVenueWillEnter(venue);
    }

    @Override
    public void onVenueExit(@NonNull Venue venue) {
        presenter.onVenueExit(venue);
    }

    @Override
    public void onClickEvent(@NonNull ClickEvent clickEvent) {
        if (clickEvent.getEventType() == ClickEvent.VENUE_CLICK) {
            mapwizeMap.centerOnVenue(clickEvent.getVenuePreview(), 300);
        }
        presenter.onClickEvent(clickEvent);
    }

    @Override
    public void onFollowUserModeChange(@NonNull FollowUserMode followUserMode) {
        presenter.onFollowUserModeChange(followUserMode);
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
