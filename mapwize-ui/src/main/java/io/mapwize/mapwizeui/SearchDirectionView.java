package io.mapwize.mapwizeui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizeui.modeview.ModeView;
import io.mapwize.mapwizeui.modeview.ModeViewAdapter;

/**
 * Search direction module
 * Include all component used to search direction and set accessibility option
 */
public class SearchDirectionView extends ConstraintLayout implements
        ModeViewAdapter.OnModeChangeListener {

    private SearchDirectionListener listener;
    private ConstraintLayout mapwizeDirectionMainLayout;
    private EditText fromEditText;
    private ImageButton clearTextFrom;
    private ImageButton clearTextTo;
    private EditText toEditText;
    private ImageView backButton;
    private ImageView swapButton;
    private boolean isSearching = false;
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
        clearTextFrom = findViewById(R.id.clearTextFrom);
        toEditText = findViewById(R.id.mapwizeDirectionToEditText);
        clearTextTo = findViewById(R.id.clearTextTo);
        backButton = findViewById(R.id.mapwizeDirectionBarBackButton);
        swapButton = findViewById(R.id.mapwizeDirectionBarSwapButton);
        swapButton.setOnClickListener(v -> listener.onDirectionSwapClick());
        backButton.setOnClickListener(v -> backClick());
        modeView = findViewById(R.id.mapwizeModeView);
        modeView.setListener(this);
        fromEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If from edit text has focus, setup from search ui
            if (hasFocus) {
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field_selected));
                setupFromSearch();
                listener.onDirectionFromFieldGetFocus();
                clearTextFrom.setVisibility(fromEditText.getText().length() > 0 ? VISIBLE : INVISIBLE);
            } else {
                clearTextFrom.setVisibility(INVISIBLE);
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field));
                // If no textfield have focus, close the keyboard
                if (!toEditText.hasFocus()) {
                    setupDefault();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    clearTextFrom.setVisibility(fromEditText.getText().length() > 0 ? VISIBLE : INVISIBLE);
                    listener.onDirectionFromQueryChange(s.toString());
                } else {
                    clearTextFrom.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearTextFrom.setOnClickListener(view -> fromEditText.setText(""));

        toEditText.setOnFocusChangeListener((v, hasFocus) -> {
            // If to edit text has focus, setup from search ui
            if (hasFocus) {
                listener.onDirectionToFieldGetFocus();
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field_selected));
                setupToSearch();
                clearTextTo.setVisibility(toEditText.getText().length() > 0 ? VISIBLE : INVISIBLE);
            } else {
                clearTextTo.setVisibility(INVISIBLE);
                v.setBackground(getContext().getDrawable(R.drawable.mapwize_rounded_field));
                // If no textfield have focus, close the keyboard
                if (!fromEditText.hasFocus()) {
                    setupDefault();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });

        clearTextTo.setOnClickListener(view -> toEditText.setText(""));

        toEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (toEditText.hasFocus()) {
                    clearTextTo.setVisibility(toEditText.getText().length() > 0 ? VISIBLE : INVISIBLE);
                    listener.onDirectionToQueryChange(s.toString());
                } else {
                    clearTextTo.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Set SearchDirectionListener to listen component events
     * @param listener the SearchDirectionListener
     */
    public void setListener(SearchDirectionListener listener) {
        this.listener = listener;
    }

    /**
     * Change the accessibility
     * @param mode determine if the direction should be accessible to low mobility people
     */
    public void setDirectionMode(DirectionMode mode) {
        this.mode = mode;
        this.modeView.setMode(mode);
    }

    public void centerOnActiveMode() {
        modeView.centerOnActiveMode();
    }

    /**
     * Show the result list
     */
    private void setupInSearch() {
        swapButton.setVisibility(View.INVISIBLE);
        isSearching = true;
        mapwizeDirectionMainLayout.setBackgroundColor(Color.argb(255, 238, 238, 238));
    }

    /**
     * Handle back click
     * If in search mode, close the search mode
     * If not in search mode, call listener.onDirectionBackClick()
     */
    private void backClick() {
        listener.onDirectionBackClick();
    }

    /**
     * Hide the result list
     */
    private void setupDefault() {
        isSearching = false;
        swapButton.setVisibility(View.VISIBLE);
        mapwizeDirectionMainLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    public void showSwapButton() {
        swapButton.setVisibility(View.VISIBLE);
    }

    public void hideSwapButton() {
        swapButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Configure module and ui to perform search query for the from field
     */
    void setupFromSearch() {
        if (fromEditText.getText().toString().equals(getResources().getString(R.string.mapwize_current_location))) {
            fromEditText.setText("");
        }
        setupInSearch();
    }

    /**
     * Configure module and ui to perform search query for the to field
     */
    void setupToSearch() {
        setupInSearch();
        if (toEditText.getText().toString().equals(getResources().getString(R.string.mapwize_current_location))) {
            toEditText.setText("");
        }
    }

    public void setModes(List<DirectionMode> modes) {
        modeView.setModes(modes);
    }

    public void setMode(DirectionMode mode) {
        modeView.setMode(mode);
    }

    public void openFromSearch() {
        fromEditText.requestFocus();
        InputMethodManager imm =(InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(fromEditText, InputMethodManager.SHOW_IMPLICIT);
        listener.onDirectionFromQueryChange("");
    }

    public void openToSearch() {
        toEditText.requestFocus();
        InputMethodManager imm =(InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(toEditText, InputMethodManager.SHOW_IMPLICIT);
        listener.onDirectionToQueryChange("");
    }

    public void setFromTitle(DirectionPoint from, String language) {
        fromEditText.clearFocus();
        setTextViewValue(fromEditText, from, language);
    }

    public void setToTitle(DirectionPoint to, String language) {
        setTextViewValue(toEditText, to, language);
        toEditText.clearFocus();
    }

    private void setTextViewValue(TextView textView, DirectionPoint directionPoint, String language) {
        if (directionPoint == null) {
            textView.setText("");
            return;
        }
        if (directionPoint instanceof Place || directionPoint instanceof Placelist) {
            MapwizeObject mapwizeObject = (MapwizeObject) directionPoint;
            textView.setText(mapwizeObject.getTranslation(language).getTitle());
        } else if (directionPoint instanceof MapwizeIndoorLocation) {
            textView.setText(getResources().getString(R.string.mapwize_current_location));
        } else if (directionPoint instanceof PlacePreview) {
            PlacePreview placePreview = (PlacePreview) directionPoint;
            textView.setText(placePreview.getTitle());
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
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            SearchDirectionView.super.setVisibility(visibility);
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
                            SearchDirectionView.super.setVisibility(visibility);
                        }
                    })
                    .start();
        }
    }

    @Override
    public void onModeChange(DirectionMode mode) {
        listener.onDirectionModeChange(mode);
    }

    public interface SearchDirectionListener {
        void onDirectionBackClick();
        void onDirectionSwapClick();
        void onDirectionFromQueryChange(String query);
        void onDirectionToQueryChange(String query);
        void onDirectionModeChange(DirectionMode mode);
        void onDirectionFromFieldGetFocus();
        void onDirectionToFieldGetFocus();
    }
}
