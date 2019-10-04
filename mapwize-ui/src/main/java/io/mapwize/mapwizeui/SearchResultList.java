package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

/**
 * Display the result of a search
 */
public class SearchResultList extends ConstraintLayout implements SearchResultAdapter.OnItemClickListener {

    private SearchResultListListener listener;
    private RecyclerView resultRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private CardView currentLocationCardView;
    private CardView noResultCardView;

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
        noResultCardView = findViewById(R.id.mapwize_no_result_card);
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

    public void showNoResultCard() { noResultCardView.setVisibility(View.VISIBLE); }

    public void hideNoResultCard() { noResultCardView.setVisibility(View.GONE); }

    /**
     * Show data in the result list
     * @param objects list of mapwize object to display
     */
    public void showData(List<? extends MapwizeObject> objects) {
        searchResultAdapter.swapData(objects);
        if (objects.size() == 0) {
            showNoResultCard();
        }
        else {
            hideNoResultCard();
        }
    }

    /**
     * Show data in the result list grouped by universe
     * @param objects list of mapwize object to display
     * @param universes list of available universe
     * @param currentUniverse current universe
     */
    public void showData(List objects, List<Universe> universes, Universe currentUniverse) {
        searchResultAdapter.swapData(objects, universes, currentUniverse);
        if (objects.size() == 0) {
            showNoResultCard();
        }
        else {
            hideNoResultCard();
        }
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

    public void onSearchResult(Placelist placelist) {
        if (listener != null) {
            listener.onSearchResult(placelist);
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
        void onSearchResult(Placelist placelist);
        void onSearchResult(Venue venue);
    }
}
