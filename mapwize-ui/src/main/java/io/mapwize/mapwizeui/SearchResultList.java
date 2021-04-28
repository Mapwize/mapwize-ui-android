package io.mapwize.mapwizeui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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
    private ProgressBar progressBar;

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

    public static final String suggestionList_KEY = "suggestionListKey";
    public static final String language_KEY = "languageState";
    public static final String indexForUniverses_KEY = "indexForUniversesKey";
    public static final String universeById_KEY = "universeByIdKey";
    public static final String universes_KEY = "universesKey";

    public void onCreate(Bundle savedInstanceState) {
        ArrayList suggestionList = savedInstanceState.getParcelableArrayList(suggestionList_KEY);
        String language = (String) savedInstanceState.getSerializable(language_KEY);
        searchResultAdapter.setLanguage(language);
        searchResultAdapter.swapData(suggestionList);

        ArrayList indexForUniverses = savedInstanceState.getParcelableArrayList(indexForUniverses_KEY);
        searchResultAdapter.setIndexForUniverses(indexForUniverses);

        HashMap universeById = (HashMap) savedInstanceState.getSerializable(universeById_KEY);
        searchResultAdapter.setUniverseById(universeById);

        ArrayList universes = savedInstanceState.getParcelableArrayList(universes_KEY);
        searchResultAdapter.setUniverses(universes);

    }

    public void onSaveInstanceState(Bundle outState) {
        String language = searchResultAdapter.getLanguage();
        outState.putSerializable(language_KEY, language);
        ArrayList suggestionList = (ArrayList) searchResultAdapter.getmSearchSuggestions();
        outState.putParcelableArrayList(suggestionList_KEY, suggestionList);

        ArrayList indexForUniverses = (ArrayList) searchResultAdapter.getIndexForUniverses();
        outState.putParcelableArrayList(indexForUniverses_KEY, indexForUniverses);

        HashMap universeById = (HashMap) searchResultAdapter.getUniverseById();
        outState.putSerializable(universeById_KEY, universeById);

        ArrayList universes = (ArrayList) searchResultAdapter.getUniverses();
        outState.putParcelableArrayList(universes_KEY, universes);
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
                listener.onCurrentLocationClick();
            }
        });
        noResultCardView = findViewById(R.id.mapwize_no_result_card);
        progressBar = findViewById(R.id.mapwizeResultListProgressBar);
    }

    public void showLoading() {
        progressBar.setVisibility(VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(INVISIBLE);
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
        if (objects != null) {
            if (objects.size() == 0) {
                showNoResultCard();
            }
            else {
                hideNoResultCard();
            }
        }
    }

    /**
     * Show data in the result list grouped by universe
     * @param objects list of mapwize object to display
     * @param universes list of available universe
     * @param currentUniverse current universe
     */

    private static final String TAG = "SearchResultList";
    public void showData(List objects, List<Universe> universes, Universe currentUniverse) {
        if (objects != null) {
            searchResultAdapter.swapData(objects, universes, currentUniverse);
            if (objects.size() == 0) {
                showNoResultCard();
            }
        } else {
            hideNoResultCard();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == getVisibility()) {
            return;
        }
        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            this.animate()
                    .translationY(this.getHeight())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            SearchResultList.super.setVisibility(visibility);
                        }
                    })
                    .start();
        }
        else {
            this.animate()
                    .translationY(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            SearchResultList.super.setVisibility(visibility);
                        }
                    })
                    .start();
        }
    }

    /**
     * Show empty
     */
    public void show() {
        setVisibility(View.VISIBLE);
//        searchResultAdapter.swapData(new ArrayList());// TODO see  why this is needed
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
        void onCurrentLocationClick();
        void onSearchResult(Place place, Universe universe);
        void onSearchResult(Placelist placelist);
        void onSearchResult(Venue venue);
    }
}
