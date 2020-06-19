package io.mapwize.mapwizeui;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.MapOptions;

public class MapwizeFragment extends Fragment {

    // Options
    private static String ARG_OPTIONS = "param_options";
    private static String ARG_UI_SETTINGS = "param_ui_settings";
    private static String ARG_MAPWIZE_CONFIGURATION = "param_mapwize_configuration";

    // Component initialization params
    private MapOptions initializeOptions = null;
    private MapwizeFragmentUISettings initializeUiSettings = null;
    private Place initializePlace = null;
    private MapwizeConfiguration mapwizeConfiguration;

    private MapwizeUIView mapwizeUIView;

    /**
     * Create a instance of MapwizeUIView
     * @param mapOptions used to setup the map
     * @return a new instance of MapwizeUIView
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
     * Create a instance of MapwizeUIView
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @return a new instance of MapwizeUIView
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
     * Create a instance of MapwizeUIView
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @return a new instance of MapwizeUIView
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
     * Create a instance of MapwizeUIView
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @return a new instance of MapwizeUIView
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
     * Create a instance of MapwizeUIView
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @param mapboxMapOptions used to pass Mapbox options at start
     * @return a new instance of MapwizeUIView
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
     * Create a instance of MapwizeUIView
     * @param mapwizeConfiguration use to setup de sdk configuration
     * @param mapOptions used to setup the map
     * @param uiSettings used to display/hide UI elements
     * @param mapboxMapOptions used to pass Mapbox options at start
     * @return a new instance of MapwizeUIView
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
        if (initializeOptions == null) {
            initializeOptions = new MapOptions.Builder().build();
        }
        if (initializeUiSettings == null) {
            initializeUiSettings = new MapwizeFragmentUISettings.Builder().build();
        }
        if (mapwizeConfiguration == null) {
            mapwizeConfiguration = MapwizeConfiguration.getInstance();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(container.getContext(), "pk.mapwize");
        return inflater.inflate(R.layout.mapwize_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapwizeUIView = new MapwizeUIView(view.getContext(), initializeOptions, initializeUiSettings, mapwizeConfiguration);
        mapwizeUIView.setListener((MapwizeUIView.OnViewInteractionListener) view.getContext());
        FrameLayout layout = view.findViewById(R.id.mapViewContainer);
        layout.addView(mapwizeUIView);
        mapwizeUIView.onCreate(savedInstanceState);
    }

    @Override
    public void onInflate(@Nullable Context context, @Nullable AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof MapwizeUIView.OnViewInteractionListener)) {
            throw new RuntimeException(context.toString() + " must implement OnViewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void selectPlace(Place place, boolean centerOn) {
        mapwizeUIView.selectPlace(place, centerOn);
    }

    /**
     * Set a direction on Mapwize UI will display the direction and the user interface
     * @param direction to display
     * @param from the starting point
     * @param to the destination point
     * @param directionMode used to find the direction
     */
    public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode) {
        mapwizeUIView.setDirection(direction, from, to, directionMode);
    }

    /**
     * Helper method to get access and refresh the UI
     * @param accesskey
     * @param callback called when the method is ended
     */
    public void grantAccess(String accesskey, ApiCallback<Boolean> callback) {
        mapwizeUIView.grantAccess(accesskey, callback);
    }

    /**
     * Getter for UI Component
     */
    public ConstraintLayout getMainLayout() {
        return mapwizeUIView.getMainLayout();
    }

    public CompassView getCompassView() {
        return mapwizeUIView.getCompassView();
    }

    public FollowUserButton getFollowUserButton() {
        return mapwizeUIView.getFollowUserButton();
    }

    public FloorControllerView getFloorControllerView() {
        return mapwizeUIView.getFloorControllerView();
    }

    public SearchBarView getSearchBarView() {
        return mapwizeUIView.getSearchBarView();
    }

    public SearchDirectionView getSearchDirectionView() {
        return mapwizeUIView.getSearchDirectionView();
    }

    public LanguagesButton getLanguagesButton() {
        return mapwizeUIView.getLanguagesButton();
    }

    public UniversesButton getUniversesButton() {
        return mapwizeUIView.getUniversesButton();
    }

    public BottomCardView getBottomCardView() {
        return mapwizeUIView.getBottomCardView();
    }

    public SearchResultList getSearchResultList() {
        return mapwizeUIView.getSearchResultList();
    }

    public FrameLayout getHeaderLayout() { return mapwizeUIView.getHeaderLayout(); }

    @Override
    public void onStart() {
        super.onStart();
        if (mapwizeUIView != null) {
            mapwizeUIView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapwizeUIView != null) {
            mapwizeUIView.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapwizeUIView != null) {
            mapwizeUIView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapwizeUIView != null) {
            mapwizeUIView.onLowMemory();
        }
    }

    @Override
    public void onPause() {
        if (mapwizeUIView != null) {
            mapwizeUIView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapwizeUIView != null) {
            mapwizeUIView.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapwizeUIView != null) {
            mapwizeUIView.onDestroy();
        }
    }

}
