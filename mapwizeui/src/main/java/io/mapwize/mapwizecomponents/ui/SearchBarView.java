package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.PlaceList;
import io.mapwize.mapwizeformapbox.api.SearchParams;
import io.mapwize.mapwizeformapbox.api.Universe;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.MapOptions;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Floating search bar.
 * Include a left button for menu, a direction button and search text field.
 */
public class SearchBarView extends ConstraintLayout implements MapwizePlugin.OnVenueEnterListener,
        SearchResultList.SearchResultListListener, MapwizePlugin.OnVenueExitListener {

    private SearchBarListener listener;
    private MapwizePlugin mapwizePlugin;
    private ImageView leftImageView;
    private ImageView backImageView;
    private FrameLayout rightImageView;
    private EditText searchEditText;
    private ConstraintLayout mainLayout;
    private SearchResultList resultList;
    private ProgressBar resultProgressBar;
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
        resultProgressBar = findViewById(R.id.mapwizeResultListProgress);
        rightImageView = findViewById(R.id.mapwizeSearchBarRightFrame);
        rightImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRightButtonClick(v);
            }
        });
        searchEditText = findViewById(R.id.mapwizeSearchBarEditText);
        backImageView.setOnClickListener(v -> setupDefault());
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
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
        if (mapwizePlugin == null) {
            return;
        }
        MapOptions options = mapwizePlugin.getMapOptions();
        searchDataManager = new SearchDataManager();
        SearchParams params = new SearchParams.Builder()
                .setObjectClass(new String[]{"venue"})
                .setOrganizationId(options.getRestrictContentToOrganizationId())
                .setVenueId(options.getRestrictContentToVenueId())
                .build();
        Api.search(params, new ApiCallback<List<MapwizeObject>>() {
            @Override
            public void onSuccess(List<MapwizeObject> mapwizeObjects) {
                searchDataManager.setVenuesList(mapwizeObjects);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

    public void setListener(SearchBarListener listener) {
        this.listener = listener;
    }

    public void setResultList(SearchResultList resultList) {
        this.resultList = resultList;
        this.resultList.setListener(this);
        this.resultList.setLanguage(mapwizePlugin.getLanguage());
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
        if (mapwizePlugin == null) {
            return;
        }
        if (!isSearching) {
            return;
        }
        resultProgressBar.setVisibility(View.VISIBLE);

        if (query.length() == 0) {
            performEmptySearch();
            return;
        }

        SearchParams.Builder builder = new SearchParams.Builder();
        builder.setQuery(query);

        // If we are in a venue, search for venue content
        if (mapwizePlugin.getVenue() != null) {
            // Filter object by type
            builder.setObjectClass(new String[]{"place", "placeList"});
            // Filter object for the current venue
            builder.setVenueId(mapwizePlugin.getVenue().getId());
            SearchParams params = builder.build();
            // Api Call
            Api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
                    new Handler(Looper.getMainLooper()).post(() -> {
                        resultList.showData(mapwizeObjects, mapwizePlugin.getVenue().getUniverses(), mapwizePlugin.getUniverse());
                        resultProgressBar.setVisibility(View.INVISIBLE);
                    });
                }

                @Override
                public void onFailure(Throwable throwable) {
                }
            });
        }
        // If we are not in a venue, search for venue
        else {
            // Filter by object type
            builder.setObjectClass(new String[]{"venue"});
            // Filter by organization if present in map options
            builder.setOrganizationId(mapwizePlugin.getMapOptions().getRestrictContentToOrganizationId());
            // Filter by venue if present in map options
            builder.setVenueId(mapwizePlugin.getMapOptions().getRestrictContentToVenueId());
            SearchParams params = builder.build();
            // Api call
            Api.search(params, new ApiCallback<List<MapwizeObject>>() {
                @Override
                public void onSuccess(final List<MapwizeObject> mapwizeObjects) {
                    // Display the result
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
        if (mapwizePlugin.getVenue() == null) {
            resultList.showData(searchDataManager.venuesList);
            resultProgressBar.setVisibility(View.INVISIBLE);
        }
        else {
            resultList.showData(searchDataManager.mainSearch, mapwizePlugin.getVenue().getUniverses(), mapwizePlugin.getUniverse());
            resultProgressBar.setVisibility(View.INVISIBLE);
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
        if (mapwizePlugin.getVenue() != null) {
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
     * @param mapwizePlugin used to listen enter and exit venue events
     */
    public void setMapwizePlugin(MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        this.mapwizePlugin.addOnVenueEnterListener(this);
        this.mapwizePlugin.addOnVenueExitListener(this);
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
        searchEditText.setHint(String.format(searchPlaceHolder, venue.getTranslation(mapwizePlugin.getLanguage()).getTitle()));
        searchEditText.setEnabled(true);
        resultProgressBar.setVisibility(View.INVISIBLE);
        rightImageView.setVisibility(View.VISIBLE);
    }

    /**
     * Will enter venue is called by mapwize sdk
     * Show loading progress
     * Load data in search data manager
     * @param venue
     */
    @Override
    public void willEnterInVenue(@NonNull Venue venue) {
        String loadingPlaceHolder = getResources().getString(R.string.loading_venue_placeholder);
        searchEditText.setHint(String.format(loadingPlaceHolder, venue.getTranslation(mapwizePlugin.getLanguage()).getTitle()));
        searchEditText.setEnabled(false);
        resultProgressBar.setVisibility(View.VISIBLE);

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

    /**
     * On venue exit is called by mapwize sdk.
     * Hide direction button and change placeholder
     * @param venue
     */
    @Override
    public void onVenueExit(@NonNull Venue venue) {
        searchEditText.setHint(getResources().getString(R.string.search_venue));
        rightImageView.setVisibility(View.GONE);
        resultProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSearchResultNull() {
        // Nothing
    }

    @Override
    public void onSearchResult(Place place, Universe universe) {
        setupDefault();
        listener.onSearchResult(place, universe);
    }

    @Override
    public void onSearchResult(PlaceList placeList) {
        setupDefault();
        listener.onSearchResult(placeList);
    }

    @Override
    public void onSearchResult(Venue venue) {
        setupDefault();
        listener.onSearchResult(venue);
    }

    public interface SearchBarListener {
        void onSearchResult(Place place, Universe universe);
        void onSearchResult(PlaceList placeList);
        void onSearchResult(Venue venue);
        void onLeftButtonClick(View view);
        void onRightButtonClick(View view);
    }

}
