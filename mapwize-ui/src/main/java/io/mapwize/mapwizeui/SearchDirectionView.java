package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.SearchParams;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.DirectionOptions;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizesdk.map.NavigationException;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.OnNavigationUpdateListener;
import io.mapwize.mapwizeui.events.Channel;
import io.mapwize.mapwizeui.events.EventManager;
import io.mapwize.mapwizeui.modeview.ModeView;

/**
 * Search direction module
 * Include all component used to search direction and set accessibility option
 */
public class SearchDirectionView extends ConstraintLayout implements
        MapwizeMap.OnVenueEnterListener,
        MapwizeMap.OnVenueExitListener {

    private SearchDirectionListener listener;
    private DirectionInfoView directionInfoView;
    private ConstraintLayout mapwizeDirectionMainLayout;
    private SearchResultList resultList;
    EditText fromEditText;
    EditText toEditText;
    private ProgressBar resultProgressBar;
    private ImageView backButton;
    private ImageView swapButton;
    private MapwizeMap mapwizeMap;
    private boolean isSearching = false;
    private SearchDataManager searchDataManager;
    private ModeView modeView;

    private DirectionPoint fromDirectionPoint;
    private DirectionPoint toDirectionPoint;
    private DirectionMode mode;

    public SearchDirectionView(Context context) {
        super(context);
        initialize(context);
    }

    public SearchDirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchDirectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mapwize_search_direction_bar, this);
        mapwizeDirectionMainLayout = findViewById(R.id.mapwizeDirectionMainLayout);
        fromEditText = findViewById(R.id.mapwizeDirectionFromEditText);
        toEditText = findViewById(R.id.mapwizeDirectionToEditText);
        resultProgressBar = findViewById(R.id.mapwizeDirectionProgressBar);
        backButton = findViewById(R.id.mapwizeDirectionBarBackButton);
        swapButton = findViewById(R.id.mapwizeDirectionBarSwapButton);
        swapButton.setOnClickListener(v -> swap());
        backButton.setOnClickListener(v -> backClick());
        modeView = findViewById(R.id.mapwizeModeView);
        fromEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If from edit text has focus, setup from search ui
            if (hasFocus) {
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field_selected));
                setupFromSearch();
            }
            else {
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field));
                setTextViewValue(fromEditText, fromDirectionPoint);
                // If no textfield have focus, close the keyboard
                if (!toEditText.hasFocus()) {
                    InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });

        fromEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fromEditText.hasFocus()) {
                    performFromSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If to edit text has focus, setup from search ui
            if (hasFocus) {
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field_selected));
                setupToSearch();
            }
            else {
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field));
                setTextViewValue(toEditText, toDirectionPoint);
                // If no textfield have focus, close the keyboard
                if (!fromEditText.hasFocus()) {
                    InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });

        toEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (toEditText.hasFocus()) {
                    performToSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initSearchDataManager() {
        if (mapwizeMap == null) {
            return;
        }
        searchDataManager = new SearchDataManager();
    }

    /**
     * Set SearchDirectionListener to listen component events
     * @param listener the SearchDirectionListener
     */
    public void setListener(SearchDirectionListener listener) {
        this.listener = listener;
    }


    /**
     * Set the mapwize plugin
     * @param mapwizeMap used to listen enter and exit venue event
     */
    public void setMapwizeMap(MapwizeMap mapwizeMap) {
        this.mapwizeMap = mapwizeMap;
        this.mapwizeMap.addOnVenueEnterListener(this);
        this.mapwizeMap.addOnVenueExitListener(this);
        initSearchDataManager();
    }

    /**
     * Set the view that will be used to display information (traveltime and distance)
     * @param directionInfoView the DirectionInfoView
     */
    public void setDirectionInfoView(DirectionInfoView directionInfoView) {
        this.directionInfoView = directionInfoView;
    }

    /**
     * Set the view that will be used to display search result
     * @param resultList the SearchResultList
     */
    public void setResultList(SearchResultList resultList) {
        this.resultList = resultList;
        this.resultList.setLanguage(mapwizeMap.getLanguage());
    }

    /**
     * Change the accessibility
     * @param accessible determine if the direction should be accessible to low mobility people
     */
    public void setAccessibility(boolean accessible) {
        if (accessible) {

        }
        else {

        }
        tryToStartDirection(fromDirectionPoint, toDirectionPoint, mode, true);
    }

    /**
     * Set the from field
     * @param directionPoint object that will be used as starting point
     */
    public void setFromDirectionPoint(Object directionPoint) {
        if (fromDirectionPoint instanceof Place) {
            mapwizeMap.removePromotedPlace((Place) fromDirectionPoint);
        }
        if (directionPoint == null) {
            fromDirectionPoint = null;
        }
        else if (directionPoint instanceof MapwizeIndoorLocation){
            fromDirectionPoint = (MapwizeIndoorLocation)directionPoint;
        }
        else {
            fromDirectionPoint = (DirectionPoint) directionPoint;
        }

        setTextViewValue(fromEditText, fromDirectionPoint);

        if (directionPoint instanceof Place) {
            Place place = (Place)directionPoint;
            mapwizeMap.addPromotedPlace(place);
        }

        if (toDirectionPoint == null) {
            toEditText.requestFocus();
        }
        tryToStartDirection(fromDirectionPoint, toDirectionPoint, mode, true);
    }

    /**
     * Set the to field
     * @param directionPoint object that will be used as destination
     */
    public void setToDirectionPoint(Object directionPoint) {
        if (toDirectionPoint instanceof Place) {
            mapwizeMap.removePromotedPlace((Place) toDirectionPoint);
        }
        if (directionPoint == null) {
            toDirectionPoint = null;
        }
        else if (directionPoint instanceof MapwizeIndoorLocation){
            toDirectionPoint = (MapwizeIndoorLocation)directionPoint;
        }
        else {
            toDirectionPoint = (DirectionPoint) directionPoint;
        }

        setTextViewValue(toEditText, toDirectionPoint);

        if (directionPoint instanceof Place) {
            Place place = (Place)directionPoint;
            mapwizeMap.addPromotedPlace(place);
        }
        tryToStartDirection(fromDirectionPoint, toDirectionPoint, mode, true);
    }

    /**
     * Method called when something change in the direction module (from, to, swap, accessiblity)
     * @param from starting point
     * @param to destination
     * @param mode determine if the direction should be accessible to low mobility people
     */
    private void tryToStartDirection(DirectionPoint from, DirectionPoint to, DirectionMode mode, boolean centerOnStart) {
        if (from == null ||to == null) {
            return;
        }
        resultProgressBar.setVisibility(View.VISIBLE);
        mapwizeMap.getMapwizeApi().getDirection(from, to, mode, new ApiCallback<Direction>() {
            @Override
            public void onSuccess(@NonNull final Direction object) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable runnable = () -> {
                    resultProgressBar.setVisibility(View.INVISIBLE);
                    startDirection(from, to, object, centerOnStart);
                };
                uiHandler.post(runnable);

            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable runnable = () -> {
                    resultProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), getResources().getString(R.string.direction_not_found), Toast.LENGTH_LONG).show();
                };
                uiHandler.post(runnable);
            }
        });
    }

    /**
     * Start a direction. Parameters are not used at this point but will be in a near future.
     * @param fromPoint the starting point
     * @param toPoint the destination
     * @param direction the entire direction object
     * @param centerOnStart if true, the camera will be centered on the starting point
     */
    private void startDirection(DirectionPoint fromPoint, DirectionPoint toPoint, Direction direction, boolean centerOnStart) {

        DirectionOptions.Builder optsBuilder = new DirectionOptions.Builder();
        if (!centerOnStart) {
            optsBuilder.centerOnStart(false);
            optsBuilder.displayStartingFloor(false);
            optsBuilder.displayEndMarker(true);
        }

        mapwizeMap.stopNavigation();

        if (fromPoint instanceof MapwizeIndoorLocation && mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null) {
            try {
                mapwizeMap.startNavigation(toPoint, mode, optsBuilder.build(), new OnNavigationUpdateListener() {
                    @Override
                    public boolean shouldRecomputeNavigation(@NonNull NavigationInfo navigationInfo) {
                        directionInfoView.setContent(navigationInfo);
                        return navigationInfo.getLocationDelta() > 10 && mapwizeMap.getUserLocation() != null && mapwizeMap.getUserLocation().getFloor() != null;
                    }

                    @Override
                    public void navigationWillStart() {

                    }

                    @Override
                    public void navigationDidStart() {
                        EventManager.getInstance().triggerOnDirectionStart(
                                mapwizeMap.getVenue(),
                                mapwizeMap.getUniverse(),
                                fromPoint,
                                toPoint,
                                "TMP_MODE",
                                true);
                    }

                    @Override
                    public void navigationDidFail(Throwable throwable) {
                        Toast.makeText(getContext(), getResources().getString(R.string.direction_not_found), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (NavigationException e) {
                mapwizeMap.setFollowUserMode(FollowUserMode.NONE);
                if (mapwizeMap.getDirection() != direction) {
                    mapwizeMap.removeMarkers();
                    mapwizeMap.setDirection(direction);
                    EventManager.getInstance().triggerOnDirectionStart(
                            mapwizeMap.getVenue(),
                            mapwizeMap.getUniverse(),
                            fromPoint,
                            toPoint,
                            "TMP_MODE",
                            false);
                }
                directionInfoView.setContent(direction);
            }
        }
        else {
            mapwizeMap.setFollowUserMode(FollowUserMode.NONE);
            if (mapwizeMap.getDirection() != direction) {
                mapwizeMap.removeMarkers();
                mapwizeMap.setDirection(direction);
                EventManager.getInstance().triggerOnDirectionStart(
                        mapwizeMap.getVenue(),
                        mapwizeMap.getUniverse(),
                        fromPoint,
                        toPoint,
                        "TMP_MODE",
                        false);
            }

            directionInfoView.setContent(direction);
        }

    }

    /**
     * Reverse from and to
     */
    private void swap() {
        DirectionPoint oldFrom = fromDirectionPoint;
        DirectionPoint oldTo = toDirectionPoint;
        fromDirectionPoint = null;
        toDirectionPoint = null;
        setToDirectionPoint(oldFrom);
        setFromDirectionPoint(oldTo);
    }

    /**
     * Show the result list
     */
    private void setupInSearch() {
        swapButton.setVisibility(View.INVISIBLE);
        isSearching = true;
        resultList.show();
        mapwizeDirectionMainLayout.setBackgroundColor(Color.argb(255, 238, 238, 238));
    }

    /**
     * Handle back click
     * If in search mode, close the search mode
     * If not in search mode, call listener.onBackClick()
     */
    private void backClick() {
        if (fromEditText.hasFocus()) {
            fromEditText.clearFocus();
            setTextViewValue(fromEditText, fromDirectionPoint);
        }
        if (toEditText.hasFocus()) {
            toEditText.clearFocus();
            setTextViewValue(toEditText, toDirectionPoint);
        }
        if (!isSearching && listener != null) {
            mapwizeMap.stopNavigation();
            fromDirectionPoint = null;
            toDirectionPoint = null;
            toEditText.setText("");
            fromEditText.setText("");
            directionInfoView.removeContent();
            listener.onBackClick();
        }
        if (fromDirectionPoint == null || toDirectionPoint == null) {
            listener.onBackClick();
        }
        setupDefault();

    }

    /**
     * Hide the result list
     */
    private void setupDefault() {
        isSearching = false;
        swapButton.setVisibility(View.VISIBLE);
        mapwizeDirectionMainLayout.setBackgroundColor(Color.TRANSPARENT);
        resultList.hide();
    }

    /**
     * Configure module and ui to perform search query for the from field
     */
    void setupFromSearch() {
        fromEditText.setText("");
        setupInSearch();

        resultList.setListener(new SearchResultList.SearchResultListListener() {
            @Override
            public void onSearchResultNull() {
                fromEditText.clearFocus();
                setFromDirectionPoint(new MapwizeIndoorLocation(mapwizeMap.getUserLocation()));
                if (toDirectionPoint != null) {
                    setupDefault();
                }
            }

            @Override
            public void onSearchResult(Place place, Universe universe) {
                fromEditText.clearFocus();
                setFromDirectionPoint(place);
                if (toDirectionPoint != null) {
                    setupDefault();
                }
            }

            @Override
            public void onSearchResult(Placelist placelist) {
                fromEditText.clearFocus();
                setFromDirectionPoint(placelist);
                if (toDirectionPoint != null) {
                    setupDefault();
                }
            }

            @Override
            public void onSearchResult(Venue venue) {

            }
        });

        performFromSearch(fromEditText.getText().toString());
    }

    /**
     * Perform an empty search for the from field
     */
    private void performEmptyFromSearch() {
        if (mapwizeMap.getVenue() != null) {
            resultList.showData(searchDataManager.mainFrom);
            if (mapwizeMap.getUserLocation() != null
                    && mapwizeMap.getUserLocation().getFloor() != null) {
                resultList.showCurrentLocationCard();
            }
        }
        else {
            resultList.showData(new ArrayList<>());
        }
        resultProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Perform search from the from field
     */
    private void performFromSearch(String query) {
        if (mapwizeMap == null) {
            return;
        }
        if (!isSearching) {
            return;
        }
        resultProgressBar.setVisibility(View.VISIBLE);

        if (query.length() == 0) {
            performEmptyFromSearch();
            return;
        }

        resultList.hideCurrentLocationCard();
        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);
        if (mapwizeMap.getVenue() != null) {
            builder.setObjectClass(new String[]{"place"});
            builder.setVenueId(mapwizeMap.getVenue().getId());
            builder.setUniverseId(mapwizeMap.getUniverseForVenue(mapwizeMap.getVenue()).getId());
            SearchParams params = builder.build();
            mapwizeMap.getMapwizeApi().search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects);
                        resultProgressBar.setVisibility(View.INVISIBLE);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
        else {
            resultList.showData(new ArrayList<>());
            resultProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Configure module and ui to perform search query for the to field
     */
    void setupToSearch() {
        setupInSearch();
        toEditText.setText("");
        resultList.setListener(new SearchResultList.SearchResultListListener() {
            @Override
            public void onSearchResultNull() {
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(null);
            }

            @Override
            public void onSearchResult(Place place, Universe universe) {
                String query = toEditText.getText().toString().length() > 0 ? toEditText.getText().toString() : null;
                Channel channel = query != null ? Channel.SEARCH : Channel.MAIN_SEARCHES;
                Universe sentUniverse = universe == null ? mapwizeMap.getUniverse() : universe;
                EventManager.getInstance().triggerOnContentSelect(place, mapwizeMap.getUniverse(), sentUniverse, channel, query);
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(place);
            }

            @Override
            public void onSearchResult(Placelist placelist) {
                String query = toEditText.getText().toString().length() > 0 ? toEditText.getText().toString() : null;
                Channel channel = query != null ? Channel.SEARCH : Channel.MAIN_SEARCHES;
                EventManager.getInstance().triggerOnContentSelect(placelist, mapwizeMap.getUniverse(), mapwizeMap.getUniverse(), channel, query);
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(placelist);
            }

            @Override
            public void onSearchResult(Venue venue) {

            }
        });
        performToSearch(toEditText.getText().toString());
    }

    /**
     * Perform an empty search for the to field
     */
    private void performEmptyToSearch() {
        if (mapwizeMap.getVenue() != null) {
            resultList.showData(searchDataManager.mainSearch);
        }
        else {
            resultList.showData(new ArrayList<>());
        }
        resultProgressBar.setVisibility(View.INVISIBLE);
        resultList.hideCurrentLocationCard();
    }

    /**
     * Perform search for the to field
     */
    private void performToSearch(String query) {
        if (mapwizeMap == null) {
            return;
        }
        if (!isSearching) {
            return;
        }
        resultProgressBar.setVisibility(View.VISIBLE);

        if (query.length() == 0) {
            performEmptyToSearch();
            return;
        }

        resultList.hideCurrentLocationCard();
        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);
        if (mapwizeMap.getVenue() != null) {
            builder.setObjectClass(new String[]{"place", "placeList"});
            builder.setVenueId(mapwizeMap.getVenue().getId());
            builder.setUniverseId(mapwizeMap.getUniverseForVenue(mapwizeMap.getVenue()).getId());
            SearchParams params = builder.build();
            mapwizeMap.getMapwizeApi().search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects);
                        resultProgressBar.setVisibility(View.INVISIBLE);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
        else {
            resultList.showData(new ArrayList<>());
            resultProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        modeView.setModes(mapwizeMap.getDirectionModes());
    }

    /**
     * Load data in searchDataManager to improve result list reactivity on empty search
     * @param venue
     */
    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {
        searchDataManager.setMainSearch(new ArrayList<>());
        searchDataManager.setMainFrom(new ArrayList<>());
        mapwizeMap.getMapwizeApi().getMainSearchesForVenue(venue.getId(), new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull List<MapwizeObject> mapwizeObjects) {
                searchDataManager.setMainSearch(mapwizeObjects);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {

            }
        });
        mapwizeMap.getMapwizeApi().getMainFromsForVenue(venue.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(@NonNull List<Place> mapwizeObjects) {
                searchDataManager.setMainFrom(mapwizeObjects);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {

            }
        });
    }

    @Override
    public void onVenueExit(@NonNull Venue venue) {

    }

    private void setTextViewValue(TextView textView, DirectionPoint directionPoint) {
        if (directionPoint == null) {
            textView.setText("");
        }
        else if (directionPoint instanceof Place || directionPoint instanceof Placelist) {
            MapwizeObject mapwizeObject = (MapwizeObject) directionPoint;
            textView.setText(mapwizeObject.getTranslation(mapwizeMap.getLanguage()).getTitle());
        }
        else if (directionPoint instanceof MapwizeIndoorLocation) {
            textView.setText(getResources().getString(R.string.current_location));
        }
    }

    public interface SearchDirectionListener {
        void onBackClick();

    }
}
