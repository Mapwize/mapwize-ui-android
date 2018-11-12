package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.Direction;
import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.PlaceList;
import io.mapwize.mapwizeformapbox.api.Translation;
import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.NavigationInfo;

/**
 * Display information about place, placelist or direction
 */
public class BottomCardView extends CardView implements MapwizeObjectInfoView, DirectionInfoView{

    private BottomCardListener listener;
    private MapwizeFragment.UIBehaviour componentsFunctions;

    private FrameLayout objectInfoFrameLayout;
    private FrameLayout directionFrameLayout;
    private ImageView titleImageView;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ConstraintLayout directionButton;
    private ConstraintLayout informationsButton;
    private TextView directionTimeTextView;
    private TextView directionDistanceTextView;

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
    }

    /**
     * Get the display components functions object that determine if an UI Component should be
     * displayed or not
     * @return the DisplayComponentsFunctions
     */
    public MapwizeFragment.UIBehaviour getComponentsFunctions() {
        return componentsFunctions;
    }

    /**
     * Set the display components functions object that determine if an UI Component should be
     * displayed or not
     */
    public void setComponentsFunctions(MapwizeFragment.UIBehaviour componentsFunctions) {
        this.componentsFunctions = componentsFunctions;
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
    public void setContent(Place place, String language) {
        directionFrameLayout.setVisibility(View.GONE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
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

        if (componentsFunctions != null && componentsFunctions.shouldDisplayInformationButton(place)) {
            informationsButton.setVisibility(View.VISIBLE);
        }
        else {
            informationsButton.setVisibility(View.GONE);
        }
    }

    /**
     * Display information about a placeList
     * @param placeList to show information
     * @param language use to display text
     */
    public void setContent(PlaceList placeList, String language) {
        directionFrameLayout.setVisibility(View.GONE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
        Translation translation = placeList.getTranslation(language);
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
        if (componentsFunctions != null && componentsFunctions.shouldDisplayInformationButton(placeList)) {
            informationsButton.setVisibility(View.VISIBLE);
        }
        else {
            informationsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void setContent(Venue venue, String language) {
        // Do something here if you want to display info about venue
    }

    /**
     * Display information about a direction
     * @param direction to show information
     */
    @Override
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
    @Override
    public void setContent(NavigationInfo navigationInfo) {
        // TODO add behavior as soon as navigation is developed
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
    interface BottomCardListener {

        /**
         * User click on direction button
         */
        void onDirectionClick();

        /**
         * User click on information button
         */
        void onInformationClick();

    }
}
