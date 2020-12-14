package io.mapwize.mapwizeui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import io.mapwize.mapwizesdk.api.ApiCallback;
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
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.PlacePreview;

public class MapwizeUIView extends FrameLayout implements BaseUIView, SearchBarView.SearchBarListener,
        SearchResultList.SearchResultListListener, FloorControllerView.OnFloorClickListener,
        BottomCardView.BottomCardListener, SearchDirectionView.SearchDirectionListener,
        FollowUserButton.FollowUserButtonListener, CompassView.OnCompassClickListener, ClosestExitButton.ClosestExitButtonListener {

    // Options
    private static String ARG_OPTIONS = "param_options";
    private static String ARG_UI_SETTINGS = "param_ui_settings";
    private static String ARG_MAPWIZE_CONFIGURATION = "param_mapwize_configuration";

    // Component initialization params
    private MapOptions initializeOptions = null;
    private MapwizeFragmentUISettings initializeUiSettings = null;
    private Place initializePlace = null;

    // Component map & mapwize
    public MapwizeMap mapwizeMap;
    private MapwizeView mapwizeView;
    private MapwizeConfiguration mapwizeConfiguration;

    public BasePresenter presenter;

    private BottomCardView bottomCardView;
    private FloorControllerView floorControllerView;
    private UniversesButton universesButton;
    private LanguagesButton languagesButton;
    private SearchBarView searchBarView;
    private SearchResultList searchResultList;
    private SearchDirectionView searchDirectionView;
    private FollowUserButton followUserButton;
    private ClosestExitButton closestExitButton;
    private CompassView compassView;
    private ConstraintLayout mainLayout;
    private FrameLayout headerLayout;

    // Component listener
    private OnViewInteractionListener listener;
    private boolean infoVisible;

    public MapwizeUIView(Context context) {
        super(context);
    }

    public MapwizeUIView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapwizeUIView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapwizeUIView(Context context,
                         MapOptions mapOptions,
                         MapwizeFragmentUISettings settings,
                         MapwizeConfiguration mapwizeConfiguration) {
        super(context);
        setupView(context, mapOptions, settings, mapwizeConfiguration);
    }

    private void setupView(Context context,
                           MapOptions mapOptions,
                           MapwizeFragmentUISettings settings,
                           MapwizeConfiguration mapwizeConfiguration) {
        this.initializeOptions = mapOptions;
        this.initializeUiSettings = settings;
        this.mapwizeConfiguration = mapwizeConfiguration;
        LayoutInflater li = LayoutInflater.from(context);
        View cv = li.inflate(R.layout.mapwize_ui_view, null);
        this.addView(cv);
        presenter = new MapPresenter(this, mapwizeConfiguration, initializeOptions);
        mapwizeView = new MapwizeView(context, mapwizeConfiguration, initializeOptions);
        FrameLayout container = cv.findViewById(R.id.mapViewContainer);
        container.addView(mapwizeView);
        //mapwizeView.onCreate(savedInstanceState);

        // Instantiate Mapwize sdk
        mapwizeView.getMapAsync(mMap -> {
            mapwizeMap = mMap;
            mapwizeMap.getMapboxMap().getUiSettings().setCompassEnabled(false);
            presenter.onMapLoaded(mapwizeMap);
        });

        bottomCardView = cv.findViewById(R.id.mapwizeBottomCardView);
        bottomCardView.setListener(this);
        floorControllerView = cv.findViewById(R.id.mapwizeFloorController);
        floorControllerView.setListener(this);
        universesButton = cv.findViewById(R.id.mapwizeUniversesButton);
        languagesButton = cv.findViewById(R.id.mapwizeLanguagessButton);
        searchBarView = cv.findViewById(R.id.mapwizeSearchBar);
        searchBarView.setListener(this);
        searchBarView.setMenuHidden(initializeUiSettings.isMenuButtonHidden());
        searchBarView.setDirectionsQrCodeButtonHidden(initializeUiSettings.isDirectionsQrCodeHidden());
        searchResultList = cv.findViewById(R.id.mapwizeSearchResultList);
        searchResultList.setListener(this);
        searchDirectionView = cv.findViewById(R.id.mapwizeDirectionSearchBar);
        searchDirectionView.setListener(this);
        closestExitButton = cv.findViewById(R.id.mapwizeClosestExitButton);
        closestExitButton.setListener(this);
        closestExitButton.setVisibility(initializeUiSettings.isClosestExitButtonHidden() ? View.GONE : View.VISIBLE);
        followUserButton = cv.findViewById(R.id.mapwizeFollowUserButton);
        followUserButton.setListener(this);
        followUserButton.setVisibility(initializeUiSettings.isFollowUserButtonHidden() ? View.GONE : View.VISIBLE);
        compassView = cv.findViewById(R.id.mapwizeCompassView);
        mainLayout = cv.findViewById(R.id.mapwizeFragmentLayout);
        headerLayout = cv.findViewById(R.id.headerFrameLayout);
    }

    public void setListener(OnViewInteractionListener listener) {
        this.listener = listener;
    }

    public void selectPlace(Place place, boolean centerOn) {
        presenter.selectPlace(place, centerOn);
    }

    @Override
    public void showSearchBar() {
        searchBarView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchBar() {
        searchBarView.setVisibility(View.GONE);
    }

    @Override
    public void showDirectionSearchBar() {
        searchDirectionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDirectionSearchBar() {
        searchDirectionView.setVisibility(View.GONE);
    }

    @Override
    public void showOutOfVenueTitle() {
        searchBarView.showOutOfVenue();
    }

    @Override
    public void showVenueTitle(String title) {
        searchBarView.showVenueTitle(title);
    }

    @Override
    public void showVenueTitleLoading(String title) {
        searchBarView.showVenueTitleLoading(title);
    }

    @Override
    public void showUniversesSelector(List<Universe> universes) {
        if(!initializeUiSettings.isUniversesButtonHidden()) {
            universesButton.setUniverses(universes);
            universesButton.showIfNeeded();
            universesButton.setListener(universe -> {
                searchBarView.showLoading();
                presenter.onUniverseClick(universe);
            });
        }
    }

    @Override
    public void hideUniversesSelector() {
        universesButton.hide();
    }

    @Override
    public void showSearchDirectionLoading() {
        searchResultList.showLoading();
    }

    @Override
    public void hideSearchDirectionLoading() {
        searchResultList.hideLoading();
    }


    @Override
    public void showPlacePreviewInfo(PlacePreview preview, String language) {
        bottomCardView.setContent(preview);
        infoVisible = true;
    }

    @Override
    public void showPlaceInfoFromPreview(Place place, String language) {
        bottomCardView.setContentFromPreview(place, language, listener.shouldDisplayInformationButton(place));
        infoVisible = true;
    }

    @Override
    public void showPlaceInfo(Place place, String language) {
        bottomCardView.setContent(place, language, listener.shouldDisplayInformationButton(place));
        infoVisible = true;
    }

    @Override
    public void showPlacelistInfo(Placelist placelist, String language) {
        bottomCardView.setContent(placelist, language, listener.shouldDisplayInformationButton(placelist));
        infoVisible = true;
    }

    @Override
    public void hideInfo() {
        bottomCardView.removeContent();
        infoVisible = false;
    }

    @Override
    public void showVenueLoading() {
        searchResultList.showLoading();
    }

    @Override
    public void hideVenueLoading() {
        searchResultList.hideLoading();
        searchBarView.hideLoading();
    }

    @Override
    public void showSearchLoading() {
        searchResultList.showLoading();
    }

    @Override
    public void hideSearchLoading() {
        searchResultList.hideLoading();
    }

    @Override
    public void showSearch() {
        searchBarView.setupInSearch();
        searchResultList.hideCurrentLocationCard();
        searchResultList.show();
    }

    @Override
    public void hideSearch() {
        searchBarView.setupDefault();
        searchResultList.hide();
    }

    public void showDirectionLoading() {
        bottomCardView.showDirectionLoading();
    }

    @Override
    public void showDirectionInfo(Direction direction) {
        bottomCardView.setContent(direction);
    }

    @Override
    public void showNavigationInfo(NavigationInfo navigationInfo) {
        bottomCardView.setContent(navigationInfo);
    }

    @Override
    public void showSelectedDirectionFrom(DirectionPoint from, String language) {
        searchDirectionView.setFromTitle(from, language);
    }

    @Override
    public void showSelectedDirectionTo(DirectionPoint to, String language) {
        searchDirectionView.setToTitle(to, language);
    }

    @Override
    public void showAccessibleDirectionModes(List<DirectionMode> modes) {
        searchDirectionView.setModes(modes);
    }

    @Override
    public void showSelectedDirectionMode(DirectionMode mode) {
        searchDirectionView.setMode(mode);
    }

    @Override
    public void showSwapButton() {
        searchDirectionView.showSwapButton();
    }

    @Override
    public void hideSwapButton() {
        searchDirectionView.hideSwapButton();
    }

    @Override
    public void showSearchDirectionFrom() {
        searchDirectionView.openFromSearch();
    }

    @Override
    public void showSearchDirectionTo() {
        searchDirectionView.openToSearch();
    }

    public void showVenueEntered(Venue venue, String language) {
        searchBarView.showVenueEntered(venue, language);
        languagesButton.showIfNeeded();
        if(!initializeUiSettings.isUniversesButtonHidden()) {
            universesButton.showIfNeeded();
        }
    }

    public void showAccessibleFloors(List<Floor> floors) {
        if (initializeUiSettings.isFloorControllerHidden()) {
            floorControllerView.setVisibility(View.GONE);
            return;
        }
        if (listener.shouldDisplayFloorController(floors)) {
            floorControllerView.setFloors(floors);
        }
    }



    @Override
    public void showLoadingFloor(Floor floor) {
        floorControllerView.setLoadingFloor(floor);
    }

    public void showActiveFloor(Floor floor) {
        floorControllerView.setFloor(floor);
    }

    @Override
    public void showSearchResultsList() {
        searchResultList.show();
    }

    @Override
    public void hideSearchResultsList() {
        searchResultList.hide();
    }

    @Override
    public void showCurrentLocationInResult() {
        searchResultList.showCurrentLocationCard();
    }

    @Override
    public void hideCurrentLocationInResult() {
        searchResultList.hideCurrentLocationCard();
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
    public void showLanguagesSelector(List<String> languages) {
        languagesButton.setLanguages(languages);
        languagesButton.setListener(language -> presenter.onLanguageClick(language));
        languagesButton.showIfNeeded();
    }

    @Override
    public void hideLanguagesSelector() {
        languagesButton.setVisibility(View.GONE);
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
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.mapwize_display_content_error), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void showFollowUserMode(FollowUserMode mode) {
        followUserButton.onFollowUserModeChange(mode);
    }

    @Override
    public void dispatchFollowUserModeWithoutLocation() {
        listener.onFollowUserButtonClickWithoutLocation();
    }

    @Override
    public void dispatchInformationButtonClick(MapwizeObject object) {
        listener.onInformationButtonClick(object);
    }

    @Override
    public void dispatchMapwizeReady(MapwizeMap mapwizeMap) {
        if (!initializeUiSettings.isCompassHidden()) {
            compassView.setMapboxMap(mapwizeMap.getMapboxMap());
            compassView.setOnCompassClickListener(this);
            listener.onFragmentReady(mapwizeMap);
        }
        else {
            compassView.setVisibility(View.GONE);
        }

    }

    @Override
    public void showDirectionError() {
        bottomCardView.showDirectionError();
    }

    @Override
    public void refreshSearchData() {
        presenter.refreshSearchData();
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
    public void onSearchBarDirectionQrCodeButtonClick() {
        listener.onDirectionsQrButtonClick();
    }

    @Override
    public void onSearchBarBackButtonClick() {
        presenter.onSearchBackButtonClick();
    }

    @Override
    public void onCurrentLocationClick() {
        presenter.onSearchResultCurrentLocationClick();
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
        presenter.onInformationClick();
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

    @Override
    public void onFollowUserClick() {
        presenter.onFollowUserModeButtonClick();
    }

    @Override
    public void onClick(CompassView compassView) {

    }



    /**
     * Set a direction on Mapwize UI will display the direction and the user interface
     * @param direction to display
     * @param from the starting point
     * @param to the destination point
     * @param directionMode used to find the direction
     */
    public void setDirection(Direction direction, DirectionPoint from, DirectionPoint to, DirectionMode directionMode) {
        presenter.setDirection(direction, from, to, directionMode);
    }

    /**
     * Helper method to get access and refresh the UI
     * @param accesskey
     * @param callback called when the method is ended
     */
    public void grantAccess(String accesskey, ApiCallback<Boolean> callback) {
        presenter.grantAccess(accesskey, callback);
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

    public FrameLayout getHeaderLayout() { return headerLayout; }


    public void onCreate(Bundle savedInstanceState) {
        if (mapwizeView != null) {
            mapwizeView.onCreate(savedInstanceState);
        }
    }

    public void onStart() {
        if (mapwizeView != null) {
            mapwizeView.onStart();
        }
    }

    public void onResume() {
        if (mapwizeView != null) {
            mapwizeView.onResume();
        }
    }

    public void onPause() {
        if (mapwizeView != null) {
            mapwizeView.onPause();
        }
    }

    public void onStop() {
        if (mapwizeView != null) {
            mapwizeView.onStop();
        }
    }

    public void onSaveInstanceState(Bundle saveInstanceState) {
        if (mapwizeView != null) {
            mapwizeView.onSaveInstanceState(saveInstanceState);
        }
    }

    public void onLowMemory() {
        if (mapwizeView != null) {
            mapwizeView.onLowMemory();
        }
    }

    public void onDestroy() {
        if (mapwizeView != null) {
            mapwizeView.onDestroy();
        }
    }

    public boolean backButtonClick() {
        if (presenter.onBackButtonPressed()) {
            return true;
        }
        if (infoVisible) {
            hideInfo();
            return true;
        }
        return false;
    }

    @Override
    public void onClosestSortieClick() {
        listener.onClosestExitButtonClick();
    }

    public interface OnViewInteractionListener {
        default void onMenuButtonClick() {

        }
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

        default void onDirectionsQrButtonClick() {

        }

        default void onClosestExitButtonClick() {

        }
    }
}
