package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.api.Direction;
import io.mapwize.mapwizeformapbox.api.DirectionPoint;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.PlaceList;
import io.mapwize.mapwizeformapbox.api.SearchParams;
import io.mapwize.mapwizeformapbox.api.Universe;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.DirectionOptions;
import io.mapwize.mapwizeformapbox.map.FollowUserMode;
import io.mapwize.mapwizeformapbox.map.MapwizeIndoorLocation;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Search direction module
 * Include all component used to search direction and set accessibility option
 */
public class SearchDirectionView extends ConstraintLayout implements
        MapwizePlugin.OnVenueEnterListener,
        MapwizePlugin.OnVenueExitListener {

    private SearchDirectionListener listener;
    private DirectionInfoView directionInfoView;
    private ConstraintLayout mapwizeDirectionMainLayout;
    private SearchResultList resultList;
    private EditText fromEditText;
    private EditText toEditText;
    private ProgressBar resultProgressBar;
    private ImageView backButton;
    private ImageView swapButton;
    private ImageView accessibilityOnButton;
    private ImageView accessibilityOffButton;
    private MapwizePlugin mapwizePlugin;
    private boolean isSearching = false;
    private SearchDataManager searchDataManager;

    private DirectionPoint fromDirectionPoint;
    private DirectionPoint toDirectionPoint;
    private boolean isAccessible = false;

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
        accessibilityOffButton = findViewById(R.id.mapwizeDirectionAccessibilityOffButton);
        accessibilityOffButton.setOnClickListener(v -> setAccessibility(false));
        accessibilityOnButton = findViewById(R.id.mapwizeDirectionAccessibilityOnButton);
        accessibilityOnButton.setOnClickListener(v -> setAccessibility(true));
        swapButton = findViewById(R.id.mapwizeDirectionBarSwapButton);
        swapButton.setOnClickListener(v -> swap());
        backButton.setOnClickListener(v -> backClick());

        fromEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If from edit text has focus, setup from search ui
            if (hasFocus) {
                setupFromSearch();
            }
            else {
                if (fromDirectionPoint == null) {
                    fromEditText.setText("");
                }
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
                performFromSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If to edit text has focus, setup from search ui
            if (hasFocus) {
                setupToSearch();
            }
            else {
                if (toDirectionPoint == null) {
                    toEditText.setText("");
                }
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
                performToSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initSearchDataManager() {
        if (mapwizePlugin == null) {
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
     * @param mapwizePlugin used to listen enter and exit venue event
     */
    public void setMapwizePlugin(MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        this.mapwizePlugin.addOnVenueEnterListener(this);
        this.mapwizePlugin.addOnVenueExitListener(this);
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
        this.resultList.setLanguage(mapwizePlugin.getLanguage());
    }

    /**
     * Change the accessibility
     * @param accessible determine if the direction should be accessible to low mobility people
     */
    public void setAccessibility(boolean accessible) {
        if (accessible) {
            accessibilityOnButton.getDrawable().setColorFilter(getResources().getColor(R.color.mapwize_main_color), PorterDuff.Mode.SRC_ATOP);
            accessibilityOnButton.setBackground(getResources().getDrawable(R.drawable.mapwize_rounded_pink_selected_view));
            accessibilityOffButton.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            accessibilityOffButton.setBackground(getResources().getDrawable(R.drawable.mapwize_rounded_selected_view));
        }
        else {
            accessibilityOffButton.getDrawable().setColorFilter(getResources().getColor(R.color.mapwize_main_color), PorterDuff.Mode.SRC_ATOP);
            accessibilityOffButton.setBackground(getResources().getDrawable(R.drawable.mapwize_rounded_pink_selected_view));
            accessibilityOnButton.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            accessibilityOnButton.setBackground(getResources().getDrawable(R.drawable.mapwize_rounded_selected_view));
        }
        isAccessible = accessible;
        tryToStartDirection(fromDirectionPoint, toDirectionPoint, isAccessible, true);
    }

    /**
     * Set the from field
     * @param directionPoint object that will be used as starting point
     */
    public void setFromDirectionPoint(Object directionPoint) {
        if (fromDirectionPoint instanceof Place) {
            mapwizePlugin.removePromotedPlace((Place) fromDirectionPoint);
        }
        if (directionPoint == null) {
            fromDirectionPoint = null;
            fromEditText.setText(null);
        }
        else if (directionPoint instanceof MapwizeIndoorLocation){
            fromDirectionPoint = (MapwizeIndoorLocation)directionPoint;
            fromEditText.setText(getResources().getString(R.string.current_location));
        }
        else {
            fromDirectionPoint = (DirectionPoint) directionPoint;
            fromEditText.setText(((MapwizeObject) directionPoint).getTranslation(mapwizePlugin.getLanguage()).getTitle());
        }

        if (directionPoint instanceof Place) {
            Place place = (Place)directionPoint;
            mapwizePlugin.addPromotedPlace(place);
        }


        tryToStartDirection(fromDirectionPoint, toDirectionPoint, isAccessible, true);
    }

    /**
     * Set the to field
     * @param directionPoint object that will be used as destination
     */
    public void setToDirectionPoint(Object directionPoint) {
        if (toDirectionPoint instanceof Place) {
            mapwizePlugin.removePromotedPlace((Place) toDirectionPoint);
        }
        if (directionPoint == null) {
            toDirectionPoint = null;
            toEditText.setText(null);
        }
        else if (directionPoint instanceof MapwizeIndoorLocation){
            toDirectionPoint = (MapwizeIndoorLocation)directionPoint;
            toEditText.setText(getResources().getString(R.string.current_location));
        }
        else {
            toDirectionPoint = (DirectionPoint) directionPoint;
            toEditText.setText(((MapwizeObject) directionPoint).getTranslation(mapwizePlugin.getLanguage()).getTitle());
        }
        if (directionPoint instanceof Place) {
            Place place = (Place)directionPoint;
            mapwizePlugin.addPromotedPlace(place);
        }
        tryToStartDirection(fromDirectionPoint, toDirectionPoint, isAccessible, true);
    }

    /**
     * Method called when something change in the direction module (from, to, swap, accessiblity)
     * @param from starting point
     * @param to destination
     * @param isAccessible determine if the direction should be accessible to low mobility people
     */
    private void tryToStartDirection(DirectionPoint from, DirectionPoint to, boolean isAccessible, boolean centerOnStart) {
        if (from == null ||to == null) {
            return;
        }
        resultProgressBar.setVisibility(View.VISIBLE);
        Api.getDirection(from, to, isAccessible, new ApiCallback<Direction>() {
            @Override
            public void onSuccess(final Direction object) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable runnable = () -> {
                    resultProgressBar.setVisibility(View.INVISIBLE);
                    startDirection(from, to, object, centerOnStart);
                };
                uiHandler.post(runnable);

            }

            @Override
            public void onFailure(Throwable t) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                resultProgressBar.setVisibility(View.INVISIBLE);
                Runnable runnable = () -> {
                    resultProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "No direction found",
                            Toast.LENGTH_LONG).show();
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
            optsBuilder.setToStartingFloor(false);
            optsBuilder.displayEndMarker(true);
        }

        mapwizePlugin.stopNavigation();

        if (fromPoint instanceof MapwizeIndoorLocation && mapwizePlugin.getUserPosition() != null && mapwizePlugin.getUserPosition().getFloor() != null) {
            mapwizePlugin.startNavigation(direction, optsBuilder.build(), navigationInfo -> {
                if (navigationInfo.getLocationDelta() > 10 && mapwizePlugin.getUserPosition() != null && mapwizePlugin.getUserPosition().getFloor() != null) {
                    tryToStartDirection(new MapwizeIndoorLocation(mapwizePlugin.getUserPosition()), toPoint, isAccessible, false);
                }
                else {
                    directionInfoView.setContent(navigationInfo);
                }
            });
            directionInfoView.setContent(direction);
        }
        else {
            mapwizePlugin.setFollowUserMode(FollowUserMode.NONE);
            if (mapwizePlugin.getDirection() != direction) {
                mapwizePlugin.removeMarkers();
                mapwizePlugin.setDirection(direction);
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
        setFromDirectionPoint(oldTo);
        setToDirectionPoint(oldFrom);
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
            fromEditText.setText("");
            fromEditText.clearFocus();
        }
        if (toEditText.hasFocus()) {
            toEditText.setText("");
            toEditText.clearFocus();
        }
        if (!isSearching && listener != null) {
            mapwizePlugin.stopNavigation();
            fromDirectionPoint = null;
            toDirectionPoint = null;
            toEditText.setText("");
            fromEditText.setText("");
            directionInfoView.removeContent();
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
    private void setupFromSearch() {
        fromEditText.setText("");
        fromDirectionPoint = null;
        setupInSearch();

        resultList.setListener(new SearchResultList.SearchResultListListener() {
            @Override
            public void onSearchResultNull() {
                fromEditText.clearFocus();
                setupDefault();
                setFromDirectionPoint(new MapwizeIndoorLocation(mapwizePlugin.getUserPosition()));
            }

            @Override
            public void onSearchResult(Place place, Universe universe) {
                fromEditText.clearFocus();
                setupDefault();
                setFromDirectionPoint(place);
            }

            @Override
            public void onSearchResult(PlaceList placeList) {
                fromEditText.clearFocus();
                setupDefault();
                setFromDirectionPoint(placeList);
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
        if (mapwizePlugin.getVenue() != null) {
            resultList.showData(searchDataManager.mainFrom);
            if (mapwizePlugin.getUserPosition() != null
                    && mapwizePlugin.getUserPosition().getFloor() != null) {
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
        if (mapwizePlugin == null) {
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
        if (mapwizePlugin.getVenue() != null) {
            builder.setObjectClass(new String[]{"place"});
            builder.setVenueId(mapwizePlugin.getVenue().getId());
            builder.setUniverseId(mapwizePlugin.getUniverseForVenue(mapwizePlugin.getVenue()).getId());
            SearchParams params = builder.build();
            Api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(final List<MapwizeObject> mapwizeObjects) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects);
                        resultProgressBar.setVisibility(View.INVISIBLE);
                    });
                }

                @Override
                public void onFailure(Throwable throwable) {
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
    private void setupToSearch() {
        setupInSearch();
        toEditText.setText("");
        toDirectionPoint = null;
        resultList.setListener(new SearchResultList.SearchResultListListener() {
            @Override
            public void onSearchResultNull() {
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(null);
            }

            @Override
            public void onSearchResult(Place place, Universe universe) {
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(place);
            }

            @Override
            public void onSearchResult(PlaceList placeList) {
                toEditText.clearFocus();
                setupDefault();
                setToDirectionPoint(placeList);
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
        if (mapwizePlugin.getVenue() != null) {
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
        if (mapwizePlugin == null) {
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
        if (mapwizePlugin.getVenue() != null) {
            builder.setObjectClass(new String[]{"place", "placeList"});
            builder.setVenueId(mapwizePlugin.getVenue().getId());
            builder.setUniverseId(mapwizePlugin.getUniverseForVenue(mapwizePlugin.getVenue()).getId());
            SearchParams params = builder.build();
            Api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(final List<MapwizeObject> mapwizeObjects) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects);
                        resultProgressBar.setVisibility(View.INVISIBLE);
                    });
                }

                @Override
                public void onFailure(Throwable throwable) {
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
        // Nothing to do here
    }

    /**
     * Load data in searchDataManager to improve result list reactivity on empty search
     * @param venue
     */
    @Override
    public void willEnterInVenue(@NonNull Venue venue) {
        searchDataManager.setMainSearch(new ArrayList<>());
        searchDataManager.setMainFrom(new ArrayList<>());
        Api.getMainSearchesForVenue(venue.getId(), new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(List<MapwizeObject> mapwizeObjects) {
                searchDataManager.setMainSearch(mapwizeObjects);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        Api.getMainFromsForVenue(venue.getId(), new ApiCallback<List<Place>>() {
            @Override
            public void onSuccess(List<Place> mapwizeObjects) {
                searchDataManager.setMainFrom(mapwizeObjects);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Override
    public void onVenueExit(@NonNull Venue venue) {

    }


    public interface SearchDirectionListener {
        void onBackClick();

    }
}
