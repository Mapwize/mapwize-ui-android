package io.mapwize.mapwizeui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.DirectionPointWrapperAndDistance;
import io.mapwize.mapwizesdk.api.DistanceResponse;
import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.PlaceDetails;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Translation;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.core.MapwizeConfiguration;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.MapwizeView;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizeui.details.ButtonBig;
import io.mapwize.mapwizeui.details.ButtonSmall;
import io.mapwize.mapwizeui.details.PlaceDetailsUI;
import io.mapwize.mapwizeui.details.Row;

public class MapwizeUIView extends FrameLayout implements BaseUIView, SearchBarView.SearchBarListener,
        SearchResultList.SearchResultListListener, FloorControllerView.OnFloorClickListener,
        BottomCardView.BottomCardListener, SearchDirectionView.SearchDirectionListener,
        FollowUserButton.FollowUserButtonListener, CompassView.OnCompassClickListener {

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
    private PlaceDetailsUI placeDetailsUI;
    private FloorControllerView floorControllerView;
    private UniversesButton universesButton;
    private LanguagesButton languagesButton;
    private SearchBarView searchBarView;
    private SearchResultList searchResultList;
    private SearchDirectionView searchDirectionView;
    private FollowUserButton followUserButton;
    private CompassView compassView;
    private ConstraintLayout mainLayout;
    private FrameLayout headerLayout;
    private float marginBottom = 24;
    private float dp;

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
        searchResultList = cv.findViewById(R.id.mapwizeSearchResultList);
        searchResultList.setListener(this);
        searchDirectionView = cv.findViewById(R.id.mapwizeDirectionSearchBar);
        searchDirectionView.setListener(this);
        followUserButton = cv.findViewById(R.id.mapwizeFollowUserButton);
        followUserButton.setListener(this);
        followUserButton.setVisibility(initializeUiSettings.isFollowUserButtonHidden() ? View.GONE : View.VISIBLE);
        compassView = cv.findViewById(R.id.mapwizeCompassView);
        mainLayout = cv.findViewById(R.id.mapwizeFragmentLayout);
        headerLayout = cv.findViewById(R.id.headerFrameLayout);
        placeDetailsUI = cv.findViewById(R.id.placeDetails);
        dp = getResources().getDisplayMetrics().density;
        placeDetailsUI.setOnSlideListener((offset, halfExpandedOffset) -> {
            if (languagesButton != null && offset < halfExpandedOffset) {
                setMarginBottom((int) (offset));
            }
        });
        placeDetailsUI.setInitalDetailsReadyListener(new PlaceDetailsUI.DetailsReadyListener() {
            @Override
            public boolean onReady(List<ButtonBig> buttonBigs, List<Row> rows, List<ButtonSmall> smallButtons) {
                for (ButtonBig buttonBig : buttonBigs) {
                    if (buttonBig.getButtonType() == ButtonSmall.DIRECTION_BUTTON) {
                        buttonBig.setOnClickListener(view -> presenter.onDirectionButtonClick());
                    }
                }
                for (ButtonSmall buttonSmall : smallButtons) {
                    if (buttonSmall.getButtonType() == ButtonSmall.DIRECTION_BUTTON) {
                        buttonSmall.setOnClickListener(view -> presenter.onDirectionButtonClick());
                    }
                }
                return true;
            }
        });
        placeDetailsUI.setStateListener(() -> {
            if (presenter != null) {
                presenter.unselectContent();
            }
        });

    }

    private void callPhoneNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
        getContext().startActivity(callIntent);
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getContext().startActivity(browserIntent);
    }

    private void setMarginBottom(int margin) {
        ConstraintLayout.LayoutParams languagesButtonLayoutParams = (ConstraintLayout.LayoutParams) languagesButton.getLayoutParams();
        languagesButtonLayoutParams.bottomMargin = (int) (margin + marginBottom * dp);
        languagesButton.setLayoutParams(languagesButtonLayoutParams);

        ConstraintLayout.LayoutParams followUserButtonLayoutParams = (ConstraintLayout.LayoutParams) followUserButton.getLayoutParams();
        followUserButtonLayoutParams.bottomMargin = (int) (margin + marginBottom * dp);
        followUserButton.setLayoutParams(followUserButtonLayoutParams);

        ConstraintLayout.LayoutParams universesButtonLayoutParams = (ConstraintLayout.LayoutParams) universesButton.getLayoutParams();
        universesButtonLayoutParams.bottomMargin = (int) (margin + marginBottom * dp);
        universesButton.setLayoutParams(universesButtonLayoutParams);
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
        universesButton.setUniverses(universes);
        universesButton.showIfNeeded();
        universesButton.setListener(universe -> {
            searchBarView.showLoading();
            presenter.onUniverseClick(universe);
        });
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
        //bottomCardView.setContent(preview);
        infoVisible = true;
        placeDetailsUI.show();
        placeDetailsUI.setLoading(true);
        placeDetailsUI.setTitle(preview.getTitle());
        if (preview.getSubtitle() != null) {
            placeDetailsUI.setSubTitle(preview.getSubtitle());
        }
        if (preview.getSubtitle() != null && !preview.getSubtitle().equals("")) {
            placeDetailsUI.setSubTitleVisibility(true);
        }
    }

    @Override
    public void showPlaceInfoFromPreview(Place place, PlaceDetails placeDetails, String language) {
        //bottomCardView.setContentFromPreview(place, language, listener.shouldDisplayInformationButton(place));
        infoVisible = true;
        showPlaceDetails(place, placeDetails, language);
    }

    private void showPlaceDetails(Place place, PlaceDetails placeDetails, String language) {
        Translation translation = placeDetails.getTranslation(language);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {//We post delayed it to wait for the bottom sheet animation
            String formatedPhoneNumber = PhoneNumberUtils.formatNumber(placeDetails.getPhone(), "fr");
            if (formatedPhoneNumber == null) {
                formatedPhoneNumber = "";
            }
            String formattedWebsite = placeDetails.getWebsite().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
            if (formattedWebsite.endsWith("/")) {
                formattedWebsite = formattedWebsite.substring(0, formattedWebsite.length() - 1);
            }

            String timezone = placeDetails.getTimezone();
            if (timezone.equals("")) {
                timezone = TimeZone.getDefault().getID();
            }
            this.placeDetailsUI.showDetails(
                    translation.getTitle(),
                    translation.getSubtitle(),
                    translation.getDetails(),
                    presenter.getFloor(),
                    placeDetails.getPhotos(),
                    placeDetails.getOpeningHours(),
                    formatedPhoneNumber,
                    formattedWebsite,
                    placeDetails.getShareLink(),
                    timezone,
                    placeDetails.getEvents(),
                    placeDetails.getCapacity(), new PlaceDetailsUI.DetailsReadyListener() {
                        @Override
                        public boolean onReady(List<ButtonBig> buttonBigs, List<Row> rows, List<ButtonSmall> smallButtons) {

                            for (Row row : rows) {
                                if (row.getRowType() == Row.PHONE_NUMBER_ROW) {
                                    if (!placeDetails.getPhone().equals("")) {
                                        row.setOnClickListener(view -> callPhoneNumber(placeDetails.getPhone()));
                                    }
                                }
                                if (row.getRowType() == Row.WEBSITE_ROW) {
                                    if (!placeDetails.getWebsite().equals("")) {
                                        row.setOnClickListener(view -> openUrl(placeDetails.getWebsite()));
                                    }
                                }
                            }

                            Iterator<ButtonSmall> iterSmallButtons = smallButtons.iterator();
                            while (iterSmallButtons.hasNext()) {
                                ButtonSmall buttonBig = iterSmallButtons.next();
                                if (buttonBig.getButtonType() == ButtonSmall.CALL_BUTTON) {
                                    if (placeDetails.getPhone().equals("")) {
                                        iterSmallButtons.remove();
                                    } else {
                                        buttonBig.setOnClickListener(view -> callPhoneNumber(placeDetails.getPhone()));
                                    }
                                }
                                if (buttonBig.getButtonType() == ButtonSmall.WEBSITE_BUTTON) {
                                    if (placeDetails.getWebsite().equals("")) {
                                        iterSmallButtons.remove();
                                    } else {
                                        buttonBig.setOnClickListener(view -> openUrl(placeDetails.getWebsite()));
                                    }
                                }
                            }

                            if (listener.shouldDisplayInformationButton(place)) {
                                ButtonSmall buttonSmall = new ButtonSmall(
                                        getContext(),
                                        "Information",
                                        R.drawable.mapwize_details_ic_baseline_info_24,
                                        false, ButtonSmall.INFORMATION_BUTTON,
                                        view -> presenter.onInformationClick()
                                );
                                smallButtons.add(buttonSmall);
                            }

                            Iterator<ButtonBig> iterBigButtons = buttonBigs.iterator();

                            while (iterBigButtons.hasNext()) {
                                ButtonBig buttonBig = iterBigButtons.next();
                                if (buttonBig.getButtonType() == ButtonSmall.CALL_BUTTON) {
                                    if (placeDetails.getPhone().equals("")) {
                                        iterBigButtons.remove();
                                    } else {
                                        buttonBig.setOnClickListener(view -> callPhoneNumber(placeDetails.getPhone()));
                                    }
                                }
                                if (buttonBig.getButtonType() == ButtonSmall.WEBSITE_BUTTON) {
                                    if (placeDetails.getWebsite().equals("")) {
                                        iterBigButtons.remove();
                                    } else {
                                        buttonBig.setOnClickListener(view -> openUrl(placeDetails.getWebsite()));
                                    }
                                }
                            }

                            Collections.sort(rows, (o1, o2) -> {
                                if (o1.isAvailable() && o2.isAvailable()) {
                                    return 0;
                                } else if (o1.isAvailable() && !o2.isAvailable()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            });
                            listener.onPlaceSelected(place, buttonBigs, rows, smallButtons);
                            return true;
                        }
                    }
            );
            placeDetailsUI.setLoading(false);
        }, 500);
    }

    @Override
    public void showPlaceInfo(Place place, PlaceDetails placeDetails, String language) {
        //bottomCardView.setContent(place, language, listener.shouldDisplayInformationButton(place));
        infoVisible = true;
        placeDetailsUI.show();
        showPlaceDetails(place, placeDetails, language);
    }

    @Override
    public void showPlacelistInfo(Placelist placelist, String language) {
        //bottomCardView.setContent(placelist, language, listener.shouldDisplayInformationButton(placelist));
        infoVisible = true;
        placeDetailsUI.show();
        placeDetailsUI.setLoading(true);
        placeDetailsUI.setTitle(placelist.getTranslation(language).getTitle());
        placeDetailsUI.setSubTitle(placelist.getTranslation(language).getSubtitle());
        if (!placelist.getTranslation(language).getSubtitle().equals("")) {
            placeDetailsUI.setSubTitleVisibility(true);
        }
        placeDetailsUI.show();

        mapwizeMap.getMapwizeApi().getPlacesForPlacelist(placelist.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(@NonNull List<Place> object) {
                MapwizeIndoorLocation userLocation = mapwizeMap.getUserLocation();
                if (userLocation != null) {
                    mapwizeMap.getMapwizeApi().getDistances(userLocation, object, mapwizeMap.getDirectionModes().get(0), true, new ApiCallback<DistanceResponse>() {
                        @Override
                        public void onSuccess(@NonNull DistanceResponse object) {
                            List<DirectionPointWrapperAndDistance> list2 = object.getDistances();
                            Iterator<DirectionPointWrapperAndDistance> iterDistanceResponse = list2.iterator();
                            while (iterDistanceResponse.hasNext()) {
                                DirectionPointWrapperAndDistance directionPointWrapperAndDistance = iterDistanceResponse.next();
                                if (directionPointWrapperAndDistance.getDistance() < 0) {
                                    iterDistanceResponse.remove();
                                }
                            }

                            Collections.sort(list2, (o1, o2) -> (int) (o1.getTraveltime() - o2.getTraveltime()));

                            List<Map<String, Object>> distances = new ArrayList<>();
                            for (DirectionPointWrapperAndDistance dpwad : list2) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("placeId", dpwad.getPlaceId());
                                map.put("venueId", dpwad.getVenueId());
                                map.put("floor", dpwad.getFloor());
                                map.put("distance", dpwad.getDistance());
                                map.put("traveltime", dpwad.getTraveltime() / 60);
                                distances.add(map);
                            }
                            placeDetailsUI.post(() -> {
                                placeDetailsUI.setTitle(placelist.getTranslation(language).getTitle());
                                placeDetailsUI.setSubTitle(placelist.getTranslation(language).getSubtitle());
                                placeDetailsUI.showPlacelist(distances, (placeId, venueId) -> {
                                    mapwizeMap.getMapwizeApi().getPlace(placeId, new ApiCallback<Place>() {
                                        @Override
                                        public void onSuccess(@NonNull Place place) {
                                            mapwizeView.post(() -> mapwizeMap.centerOnPlace(place, 200));
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            //TODO handle Failure
                                        }
                                    });
                                });
                                placeDetailsUI.setLoading(false);
                            });
                        }

                        @Override
                        public void onFailure(@NonNull Throwable t) {
                            //TODO handle Failure
                            System.out.println("Failed to get distances : " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                //TODO handle Failure
                System.out.println("Failed to get places for placelist : " + t.getMessage());
            }
        });

        //TODO listener.shouldDisplayInformationButton(placelist)
//        bottomCardView.setContent(placelist, language, listener.shouldDisplayInformationButton(placelist));
    }

    @Override
    public void hideInfo() {
        bottomCardView.removeContent();
        placeDetailsUI.hide();
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
        universesButton.showIfNeeded();
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
        } else {
            compassView.setVisibility(View.GONE);
        }

    }

    @Override
    public void setLanguage(String language) {
        this.searchResultList.setLanguage(language);
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
    public void onDetailsOpen(int height) {
        setMarginBottom(height);
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

    public FrameLayout getHeaderLayout() {
        return headerLayout;
    }


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
            presenter.unselectContent();
            return true;
        }
        return false;
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

        default boolean onPlaceSelected(Place place, List<ButtonBig> buttonBigs, List<Row> rows, List<ButtonSmall> smallButtons) {
            return false;
        }
    }
}
