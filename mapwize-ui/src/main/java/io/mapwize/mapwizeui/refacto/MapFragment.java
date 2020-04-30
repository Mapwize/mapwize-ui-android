package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.List;

import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizeui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeui.R;

public class MapFragment extends Fragment implements BaseFragment, MapwizeMap.OnVenueEnterListener,
        MapwizeMap.OnVenueExitListener, MapwizeMap.OnUniverseChangeListener, MapwizeMap.OnFloorChangeListener,
        MapwizeMap.OnFloorsChangeListener, MapwizeMap.OnDirectionModesChangeListener, MapwizeMap.OnLanguageChangeListener {

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
        Scene defaultScene = Scene.getSceneForLayout(sceneRoot, R.layout.mwz_map_scene_default, getContext());
        TransitionManager.go(defaultScene);
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


    public interface OnFragmentInteractionListener {
    }
}
