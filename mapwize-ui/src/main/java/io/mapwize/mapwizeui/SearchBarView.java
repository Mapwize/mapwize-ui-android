package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizesdk.api.ApiCallback;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.SearchParams;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.MapOptions;
import io.mapwize.mapwizesdk.map.MapwizeMap;
import io.mapwize.mapwizeui.events.Channel;
import io.mapwize.mapwizeui.events.EventManager;

/**
 * Floating search bar.
 * Include a left button for menu, a direction button and search text field.
 */
public class SearchBarView extends ConstraintLayout implements MapwizeMap.OnVenueEnterListener,
        SearchResultList.SearchResultListListener, MapwizeMap.OnVenueExitListener {

    private SearchBarListener listener;
    private MapwizeMap mapwizeMap;
    private ImageView leftImageView;
    private ImageView backImageView;
    private FrameLayout rightImageView;
    private EditText searchEditText;
    private ConstraintLayout mainLayout;
    private SearchResultList resultList;
    private SearchDataManager searchDataManager;
    private boolean menuHidden;

    private boolean isSearching = false;

    public SearchBarView(Context context) {
        super(context);
        initialize(context);
    }

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mapwize_search_bar, this);
        searchDataManager = new SearchDataManager();
        mainLayout = findViewById(R.id.mapwizeSearchMainLayout);
        backImageView= findViewById(R.id.mapwizeSearchBarBackButton);
        leftImageView = findViewById(R.id.mapwizeSearchBarLeftButton);
        leftImageView.setOnClickListener(v -> {
            setupDefault();
            if (listener != null) {
                listener.onLeftButtonClick(v);
            }
        });
        rightImageView = findViewById(R.id.mapwizeSearchBarRightFrame);
        rightImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRightButtonClick(v);
            }
        });
        searchEditText = findViewById(R.id.mapwizeSearchBarEditText);
        backImageView.setOnClickListener(v -> setupDefault());
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (this.resultList == null || this.mapwizeMap == null) {
                return;
            }
            if (hasFocus) {
                setupInSearch();
            }
            else {
                InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private void initSearchDataManager() {
        if (mapwizeMap == null) {
            return;
        }
        MapOptions options = mapwizeMap.getMapOptions();
        searchDataManager = new SearchDataManager();
        SearchParams.Builder paramsBuilder = new SearchParams.Builder()
                .setObjectClass(new String[]{"venue"})
                .setOrganizationId(options.getRestrictContentToOrganizationId());
        if (options.getRestrictContentToVenueIds() != null) {
            paramsBuilder.setVenueIds(options.getRestrictContentToVenueIds());
        }
        mapwizeMap.getMapwizeApi().search(paramsBuilder.build(), new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(@NonNull List<MapwizeObject> mapwizeObjects) {
                searchDataManager.setVenuesList(mapwizeObjects);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
            }
        });
    }

    public void setListener(SearchBarListener listener) {
        this.listener = listener;
    }

    public void setResultList(SearchResultList resultList) {
        this.resultList = resultList;
        this.resultList.setListener(this);
        this.resultList.setLanguage(mapwizeMap.getLanguage());
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Perform search and display result
     * @param query the search query
     */
    private void performSearch(String query) {
        if (mapwizeMap == null) {
            return;
        }
        if (!isSearching) {
            return;
        }

        if (query.length() == 0) {
            performEmptySearch();
            return;
        }

        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);

        // If we are in a venue, search for venue content
        if (mapwizeMap.getVenue() != null) {
            // Filter object by type
            builder.setObjectClass(new String[]{"place", "placeList"});
            // Filter object for the current venue
            builder.setVenueId(mapwizeMap.getVenue().getId());

            SearchParams params = builder.build();
            // Api Call
            mapwizeMap.getMapwizeApi().search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects, mapwizeMap.getUniverses(), mapwizeMap.getUniverse());
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
        // If we are not in a venue, search for venue
        else {
            // Filter by object type
            builder.setObjectClass(new String[]{"venue"});
            // Filter by organization if present in map options
            builder.setOrganizationId(mapwizeMap.getMapOptions().getRestrictContentToOrganizationId());
            // Filter by venue if present in map options
            if (mapwizeMap.getMapOptions().getRestrictContentToVenueIds() != null) {
                builder.setVenueIds(mapwizeMap.getMapOptions().getRestrictContentToVenueIds());
            }
            SearchParams params = builder.build();
            // Api call
            mapwizeMap.getMapwizeApi().search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(@NonNull final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable throwable) {
                }
            });
        }
    }

    public void setMenuHidden(boolean isDisplayed) {
        this.menuHidden = isDisplayed;
        if (this.menuHidden) {
            this.leftImageView.setVisibility(View.GONE);
        }
    }

    /**
     * Call if the search query is empty.
     * Get data from searchDataManager
     */
    private void performEmptySearch() {
        if (mapwizeMap.getVenue() == null) {
            resultList.showData(searchDataManager.venuesList);
        }
        else {
            resultList.showData(searchDataManager.mainSearch, mapwizeMap.getUniverses(), mapwizeMap.getUniverse());
        }
    }

    /**
     * Setup UI for in search mode
     * Hide useless component
     * Show result list
     */
    private void setupInSearch() {
        isSearching = true;
        leftImageView.setVisibility(View.GONE);
        backImageView.setVisibility(View.VISIBLE);
        rightImageView.setVisibility(View.GONE);
        mainLayout.setBackgroundColor(Color.argb(255, 238, 238, 238));
        this.setBackgroundColor(Color.argb(255, 238, 238, 238));
        resultList.hideCurrentLocationCard();
        resultList.show();
        performSearch("");

        this.setTranslationZ(2);
    }

    /**
     * Setup default UI
     * Hide search result list
     * Show search bar button
     */
    private void setupDefault() {
        isSearching = false;
        if (!menuHidden) {
            leftImageView.setVisibility(View.VISIBLE);
        }
        backImageView.setVisibility(View.GONE);
        if (mapwizeMap.getVenue() != null) {
            rightImageView.setVisibility(View.VISIBLE);
        }
        searchEditText.setText("");
        searchEditText.clearFocus();
        mainLayout.setBackgroundColor(Color.TRANSPARENT);
        this.setBackgroundColor(Color.TRANSPARENT);
        resultList.hide();
        this.setTranslationZ(0);
    }

    /**
     * Set mapwize plugin
     * @param mapwizeMap used to listen enter and exit venue events
     */
    public void setMapwizeMap(MapwizeMap mapwizeMap) {
        this.mapwizeMap = mapwizeMap;
        this.mapwizeMap.addOnVenueEnterListener(this);
        this.mapwizeMap.addOnVenueExitListener(this);
        initSearchDataManager();
    }

    /**
     * On venue enter is called by mapwize sdk
     * Change search bar UI to display direction button and place holder
     * @param venue the current venue
     */
    @Override
    public void onVenueEnter(@NonNull Venue venue) {
        String searchPlaceHolder = getResources().getString(R.string.search_in_placeholder);
        searchEditText.setHint(String.format(searchPlaceHolder, venue.getTranslation(mapwizeMap.getLanguage()).getTitle()));
        searchEditText.setEnabled(true);
        rightImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Will enter venue is called by mapwize sdk
     * Show loading progress
     * Load data in search data manager
     * @param venue
     */
    @Override
    public void onVenueWillEnter(@NonNull Venue venue) {
        String loadingPlaceHolder = getResources().getString(R.string.loading_venue_placeholder);
        searchEditText.setHint(String.format(loadingPlaceHolder, venue.getTranslation(mapwizeMap.getLanguage()).getTitle()));
        searchEditText.setEnabled(false);

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
    public void onVenueEnterError(@NonNull Venue venue, @NonNull Throwable error) {

    }

    /*@Override
    public void onVenueEnterError(Venue venue, Throwable error) {
        String searchPlaceHolder = getResources().getString(R.string.search_in_placeholder);
        searchEditText.setHint(String.format(searchPlaceHolder, venue.getTranslation(mapwizeMap.getLanguage()).getTitle()));
        searchEditText.setEnabled(true);
        resultProgressBar.setVisibility(View.INVISIBLE);
        rightImageView.setVisibility(View.VISIBLE);
    }*/

    /**
     * On venue exit is called by mapwize sdk.
     * Hide direction button and change placeholder
     * @param venue
     */
    @Override
    public void onVenueExit(@NonNull Venue venue) {
        searchEditText.setHint(getResources().getString(R.string.search_venue));
        rightImageView.setVisibility(View.GONE);
    }

    @Override
    public void onSearchResultNull() {
        // Nothing
    }

    @Override
    public void onSearchResult(Place place, Universe universe) {
        String query = searchEditText.getText().toString().length() > 0 ? searchEditText.getText().toString() : null;
        Channel channel = query != null ? Channel.SEARCH : Channel.MAIN_SEARCHES;
        Universe sentUniverse = universe == null ? mapwizeMap.getUniverse() : universe;
        EventManager.getInstance().triggerOnContentSelect(place, mapwizeMap.getUniverse(), sentUniverse, channel, query);
        setupDefault();
        listener.onSearchResult(place, universe);
    }

    @Override
    public void onSearchResult(Placelist placelist) {
        String query = searchEditText.getText().toString().length() > 0 ? searchEditText.getText().toString() : null;
        Channel channel = query != null ? Channel.SEARCH : Channel.MAIN_SEARCHES;
        EventManager.getInstance().triggerOnContentSelect(placelist, mapwizeMap.getUniverse(),mapwizeMap.getUniverse(),channel, query);
        setupDefault();
        listener.onSearchResult(placelist);
    }

    @Override
    public void onSearchResult(Venue venue) {
        setupDefault();
        listener.onSearchResult(venue);
    }

    public interface SearchBarListener {
        void onSearchResult(Place place, Universe universe);
        void onSearchResult(Placelist placelist);
        void onSearchResult(Venue venue);
        void onLeftButtonClick(View view);
        void onRightButtonClick(View view);
    }

}
