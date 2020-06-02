package io.mapwize.mapwizeui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
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

import io.mapwize.mapwizesdk.api.Venue;

/**
 * Floating search bar.
 * Include a left button for menu, a direction button and search text field.
 */
public class SearchBarView extends ConstraintLayout {

    private SearchBarListener listener;
    private ImageView leftImageView;
    private ImageView backImageView;
    private FrameLayout rightImageView;
    private EditText searchEditText;
    private ConstraintLayout mainLayout;
    private ProgressBar progressBar;
    private boolean directionButtonHidden;
    private boolean menuHidden;

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
        mainLayout = findViewById(R.id.mapwizeSearchMainLayout);
        backImageView = findViewById(R.id.mapwizeSearchBarBackButton);
        backImageView.setOnClickListener(v -> {
            setupDefault();
            listener.onSearchBarBackButtonClick();
        });
        leftImageView = findViewById(R.id.mapwizeSearchBarLeftButton);
        leftImageView.setOnClickListener(v -> {
            listener.onSearchBarMenuClick();
        });
        rightImageView = findViewById(R.id.mapwizeSearchBarRightFrame);
        rightImageView.setOnClickListener(v -> {
            listener.onSearchBarDirectionButtonClick();
        });
        searchEditText = findViewById(R.id.mapwizeSearchBarEditText);
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm =(InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                listener.onSearchStart();
            }
            else {
                InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onSearchBarQueryChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        progressBar = findViewById(R.id.mapwizeSearchBarProgressBar);
    }

    public void setDirectionButtonHidden(boolean isHidden) {
        directionButtonHidden = isHidden;
        this.rightImageView.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    public void setListener(SearchBarListener listener) {
        this.listener = listener;
    }

    public void setMenuHidden(boolean isHidden) {
        this.menuHidden = isHidden;
        if (this.menuHidden) {
            this.leftImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == getVisibility()) {
            return;
        }
        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            this.animate()
                    .translationY(-this.getHeight())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            SearchBarView.super.setVisibility(visibility);
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
                            SearchBarView.super.setVisibility(visibility);
                        }
                    })
                    .start();
        }
    }

    /**
     * Setup UI for in search mode
     * Hide useless component
     * Show result list
     */
    public void setupInSearch() {
        leftImageView.setVisibility(View.GONE);
        backImageView.setVisibility(View.VISIBLE);
        mainLayout.setBackgroundColor(Color.argb(255, 238, 238, 238));
        this.setBackgroundColor(Color.argb(255, 238, 238, 238));
        this.setTranslationZ(2);
        this.rightImageView.setVisibility(View.GONE);
    }

    /**
     * Setup default UI
     * Hide search result list
     * Show search bar button
     */
    public void setupDefault() {
        if (!menuHidden) {
            leftImageView.setVisibility(View.VISIBLE);
        }
        backImageView.setVisibility(View.GONE);
        searchEditText.setText("");
        searchEditText.clearFocus();
        mainLayout.setBackgroundColor(Color.TRANSPARENT);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setTranslationZ(0);
        this.rightImageView.setVisibility(directionButtonHidden ? View.GONE : View.VISIBLE);
    }

    public void showOutOfVenue() {
        searchEditText.setHint(getResources().getString(R.string.search_venue));
        rightImageView.setVisibility(GONE);
    }

    public void showVenueEntering(Venue venue, String language) {
        String loadingPlaceHolder = getResources().getString(R.string.loading_venue_placeholder);
        searchEditText.setHint(String.format(loadingPlaceHolder, venue.getTranslation(language).getTitle()));
        searchEditText.setEnabled(false);
    }

    public void showVenueEntered(Venue venue, String language) {
        String searchPlaceHolder = getResources().getString(R.string.search_in_placeholder);
        searchEditText.setHint(String.format(searchPlaceHolder, venue.getTranslation(language).getTitle()));
        searchEditText.setEnabled(true);
        rightImageView.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        progressBar.setVisibility(VISIBLE);
        if (backImageView.getVisibility() == VISIBLE) {
            backImageView.setVisibility(INVISIBLE);
        }
        if (leftImageView.getVisibility() == VISIBLE) {
            leftImageView.setVisibility(INVISIBLE);
        }
    }

    public void hideLoading() {
        progressBar.setVisibility(GONE);
        if (backImageView.getVisibility() == INVISIBLE) {
            backImageView.setVisibility(VISIBLE);
        }
        if (leftImageView.getVisibility() == INVISIBLE) {
            leftImageView.setVisibility(VISIBLE);
        }
    }

    public interface SearchBarListener {
        void onSearchStart();
        void onSearchBarMenuClick();
        void onSearchBarQueryChange(String query);
        void onSearchBarDirectionButtonClick();
        void onSearchBarBackButtonClick();
    }

}
