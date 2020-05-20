package io.mapwize.mapwizeui;

import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Translation;
import io.mapwize.mapwizesdk.api.Venue;
import io.mapwize.mapwizesdk.map.NavigationInfo;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizesdk.map.PreviewCallback;

/**
 * Display information about place, placelist or direction
 */
public class BottomCardView extends CardView {

    private BottomCardListener listener;
    private MapwizeFragment.OnFragmentInteractionListener interactionListener;

    private FrameLayout objectInfoFrameLayout;
    private FrameLayout directionFrameLayout;
    private ImageView titleImageView;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ConstraintLayout directionButton;
    private ConstraintLayout informationsButton;
    private TextView directionTimeTextView;
    private TextView directionDistanceTextView;
    private WebView detailsWebView;
    private ImageView closeDetailsButton;
    private ProgressBar progressBar;
    private boolean hasDetails;

    public BottomCardView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public BottomCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BottomCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mapwize_bottom_view, this);
        titleImageView = findViewById(R.id.mapwizeBottomImageView);
        titleTextView = findViewById(R.id.mapwizeBottomTitleTextView);
        subtitleTextView = findViewById(R.id.mapwizeBottomSubtitleTextView);
        objectInfoFrameLayout = findViewById(R.id.mapwizeObjectContentFrame);
        directionFrameLayout = findViewById(R.id.mapwizeDirectionContentFrame);
        directionTimeTextView = findViewById(R.id.direction_info_bottom_time_text);
        directionDistanceTextView = findViewById(R.id.direction_info_bottom_distance_text);
        directionButton = findViewById(R.id.mapwizeBottomDirectionButton);
        directionButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDirectionClick();
            }
        });
        informationsButton = findViewById(R.id.mapwizeBottomInformationsButton);
        informationsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInformationClick();
            }
        });
        detailsWebView = findViewById(R.id.mapwize_details_webview);

        objectInfoFrameLayout.setOnClickListener(v -> {
            if (hasDetails) {
                showDetails();
            }
        });
        closeDetailsButton = findViewById(R.id.mapwizeCloseDetails);
        closeDetailsButton.setOnClickListener(v -> {
            hideDetails();
        });
        progressBar = findViewById(R.id.mapwizeBottomLoader);
    }

    private void showDetails() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) this.getLayoutParams();
        lp.height = LayoutParams.MATCH_PARENT;
        this.setLayoutParams(lp);
        closeDetailsButton.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
        /*ViewGroup.LayoutParams layoutParams = objectInfoFrameLayout.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        objectInfoFrameLayout.setLayoutParams(layoutParams);*/
        //listener.onDetailsOpen();
    }

    private void hideDetails() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) this.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        this.setLayoutParams(lp);
        closeDetailsButton.setVisibility(View.GONE);
        //listener.onDetailsClose();
    }

    /**
     * Get the display components functions object that determine if an UI Component should be
     * displayed or not
     * @return the DisplayComponentsFunctions
     */
    public MapwizeFragment.OnFragmentInteractionListener getInteractionListener() {
        return interactionListener;
    }

    /**
     * Set the display components functions object that determine if an UI Component should be
     * displayed or not
     */
    public void setInteractionListener(MapwizeFragment.OnFragmentInteractionListener listener) {
        this.interactionListener = listener;
    }

    /**
     * Hide the view
     */
    public void removeContent() {
        objectInfoFrameLayout.setVisibility(View.GONE);
        directionFrameLayout.setVisibility(View.GONE);
    }

    /**
     * Display information about a place
     * @param place to show information
     * @param language use to display text
     */
    public void setContent(Place place, String language, boolean showInfoButton) {
        directionFrameLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        Translation translation = place.getTranslation(language);
        if (translation.getTitle().length() > 0) {
            titleTextView.setText(translation.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
        else {
            titleTextView.setVisibility(View.GONE);
        }
        if (translation.getSubtitle().length() > 0) {
            subtitleTextView.setText(translation.getSubtitle());
            subtitleTextView.setVisibility(View.VISIBLE);
        }
        else {
            subtitleTextView.setVisibility(View.GONE);
        }
        titleImageView.setVisibility(View.VISIBLE);

        titleImageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_location_on_black_24dp));

        if (showInfoButton) {
            informationsButton.setVisibility(View.VISIBLE);
        }
        else {
            informationsButton.setVisibility(View.GONE);
        }

        if (translation.getDetails() != null && translation.getDetails().length() > 0) {
            hasDetails = true;
            detailsWebView.loadData("<div>"+ translation.getDetails() +"</div>", null, null);
            detailsWebView.setVisibility(View.VISIBLE);
        }
        else {
            hasDetails = false;
            detailsWebView.setVisibility(View.GONE);
        }
        directionButton.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
    }

    public void setContent(PlacePreview placePreview) {
        directionFrameLayout.setVisibility(View.GONE);
        if (placePreview.getTitle().length() > 0) {
            titleTextView.setText(placePreview.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
        else {
            titleTextView.setVisibility(View.GONE);
        }
        if (placePreview.getSubtitle() != null && placePreview.getSubtitle().length() > 0) {
            subtitleTextView.setText(placePreview.getSubtitle());
            subtitleTextView.setVisibility(View.VISIBLE);
        }
        else {
            subtitleTextView.setVisibility(View.GONE);
        }
        titleImageView.setVisibility(View.VISIBLE);
        detailsWebView.loadData("", null, null);
        detailsWebView.setVisibility(GONE);
        titleImageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_location_on_black_24dp));

        informationsButton.setVisibility(View.INVISIBLE);
        //detailsWebView.setVisibility(View.INVISIBLE);
        directionButton.setVisibility(View.INVISIBLE);
        hasDetails = false;
        progressBar.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
    }

    public void setContentFromPreview(Place place, String language, boolean showInfoButton) {
        progressBar.setVisibility(View.GONE);
        if (showInfoButton) {
            informationsButton.setVisibility(View.VISIBLE);
        }
        else {
            informationsButton.setVisibility(View.GONE);
        }

        Translation translation = place.getTranslation(language);
        if (translation.getDetails() != null && translation.getDetails().length() > 0) {
            hasDetails = true;
            detailsWebView.loadData("<div>"+ translation.getDetails() +"</div>", null, null);
            detailsWebView.setVisibility(View.VISIBLE);
        }
        else {
            hasDetails = false;
            detailsWebView.setVisibility(View.GONE);
        }
        directionButton.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Display information about a placeList
     * @param placelist to show information
     * @param language use to display text
     */
    public void setContent(Placelist placelist, String language, boolean showInfoButton) {
        directionFrameLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        Translation translation = placelist.getTranslation(language);
        if (translation.getTitle().length() > 0) {
            titleTextView.setText(translation.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
        else {
            titleTextView.setVisibility(View.GONE);
        }
        if (translation.getSubtitle().length() > 0) {
            subtitleTextView.setText(translation.getSubtitle());
            subtitleTextView.setVisibility(View.VISIBLE);
        }
        else {
            subtitleTextView.setVisibility(View.GONE);
        }
        titleImageView.setVisibility(View.VISIBLE);
        if (showInfoButton) {
            informationsButton.setVisibility(View.VISIBLE);
        }
        else {
            informationsButton.setVisibility(View.GONE);
        }

        titleImageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_menu_black_24dp));

        if (translation.getDetails() != null && translation.getDetails().length() > 0) {
            hasDetails = true;
            detailsWebView.loadData("<div>"+ translation.getDetails() +"</div>", null, null);
            detailsWebView.setVisibility(View.VISIBLE);
        }
        else {
            hasDetails = false;
            detailsWebView.setVisibility(View.GONE);
        }
        directionButton.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
    }

    public void setContent(Venue venue, String language) {
        // Do something here if you want to display info about venue
    }

    /**
     * Display information about a direction
     * @param direction to show information
     */
    public void setContent(Direction direction) {
        directionFrameLayout.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.GONE);
        long time = Math.round(direction.getTraveltime() / 60);
        String timPlaceHolder = getResources().getString(R.string.time_placeholder);
        directionTimeTextView.setText(String.format(timPlaceHolder,time));
        directionDistanceTextView.setText(UnitLocale.distanceAsString(direction.getDistance()));
    }

    /**
     * Display information about a navigation
     * @param navigationInfo to show information
     */
    public void setContent(NavigationInfo navigationInfo) {
        new Handler(Looper.getMainLooper()).post(() -> {
            directionFrameLayout.setVisibility(View.VISIBLE);
            objectInfoFrameLayout.setVisibility(View.GONE);
            long time = Math.round(navigationInfo.getDuration() / 60);
            String timPlaceHolder = getResources().getString(R.string.time_placeholder);
            directionTimeTextView.setText(String.format(timPlaceHolder,time));
            directionDistanceTextView.setText(UnitLocale.distanceAsString(navigationInfo.getDistance()));
        });
    }

    /**
     * Get the BottomCardListener
     * @return the listener
     */
    public BottomCardListener getListener() {
        return listener;
    }

    /**
     * Set the BottomCardListener
     * @param listener the listener
     */
    public void setListener(BottomCardListener listener) {
        this.listener = listener;
    }

    /**
     * Interface used to listen bottom view event
     */
    public interface BottomCardListener {

        /**
         * User click on direction button
         */
        void onDirectionClick();

        /**
         * User click on information button
         */
        void onInformationClick();

        void onDetailsOpen();

        void onDetailsClose();

    }

}
