package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.PlaceList;
import io.mapwize.mapwizeformapbox.api.Universe;
import io.mapwize.mapwizeformapbox.api.Venue;

/**
 * Display the result of a search
 */
public class SearchResultList extends ConstraintLayout implements SearchResultAdapter.OnItemClickListener {

    private SearchResultListListener listener;
    private RecyclerView resultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private CardView currentLocationCardView;

    public SearchResultList(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public SearchResultList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchResultList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mapwize_search_results_list, this);
        resultRecyclerView = findViewById(R.id.mapwizeSearchResultRecyclerView);
        searchResultAdapter = new SearchResultAdapter();
        resultRecyclerView.setAdapter(searchResultAdapter);
        searchResultAdapter.setListener(this);
        currentLocationCardView = findViewById(R.id.mapwizeCurrentLocationCard);
        currentLocationCardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchResultNull();
            }
        });
    }

    /**
     * Set the language used to display result
     * @param language used
     */
    public void setLanguage(String language) {
        searchResultAdapter.setLanguage(language);
    }

    /**
     * Set the listener to handle selection
     * @param listener used
     */
    public void setListener(SearchResultListListener listener) {
        this.listener = listener;
    }

    /**
     * Show the current location button.
     * Used in from direction search if a user have an indoor location
     */
    public void showCurrentLocationCard() {
        currentLocationCardView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the current location button.
     */
    public void hideCurrentLocationCard() {
        currentLocationCardView.setVisibility(View.GONE);
    }

    /**
     * Show data in the result list
     * @param objects list of mapwize object to display
     */
    public void showData(List<? extends MapwizeObject> objects) {
        searchResultAdapter.swapData(objects);
    }

    /**
     * Show data in the result list grouped by universe
     * @param objects list of mapwize object to display
     * @param universes list of available universe
     * @param currentUniverse current universe
     */
    public void showData(List objects, List<Universe> universes, Universe currentUniverse) {
        searchResultAdapter.swapData(objects, universes, currentUniverse);
    }

    /**
     * Show empty
     */
    public void show() {
        setVisibility(View.VISIBLE);
        searchResultAdapter.swapData(new ArrayList());
        setBackgroundColor(Color.argb(255, 238, 238, 238));
    }

    /**
     * Hide the list
     */
    public void hide() {
        setVisibility(View.GONE);
    }

    public void onSearchResult(Place place, Universe universe) {
        if (listener != null) {
            listener.onSearchResult(place, universe);
        }
    }

    public void onSearchResult(PlaceList placeList) {
        if (listener != null) {
            listener.onSearchResult(placeList);
        }
    }

    public void onSearchResult(Venue venue) {
        if (listener != null) {
            listener.onSearchResult(venue);
        }
    }


    public interface SearchResultListListener {
        void onSearchResultNull();
        void onSearchResult(Place place, Universe universe);
        void onSearchResult(PlaceList placeList);
        void onSearchResult(Venue venue);
    }
}
