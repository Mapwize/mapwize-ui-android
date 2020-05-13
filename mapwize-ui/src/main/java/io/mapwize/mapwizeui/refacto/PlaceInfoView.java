package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Translation;
import io.mapwize.mapwizesdk.map.PlacePreview;
import io.mapwize.mapwizeui.MapwizeFragment;
import io.mapwize.mapwizeui.R;

public class PlaceInfoView  extends CardView {

    private PlaceInfoViewListener listener;
    private MapwizeFragment.OnFragmentInteractionListener interactionListener;

    private ConstraintLayout objectInfoFrameLayout;
    private ImageView titleImageView;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ConstraintLayout directionButton;
    private ConstraintLayout informationsButton;
    private WebView detailsWebView;
    private ImageView closeDetailsButton;
    private ProgressBar progressBar;
    private boolean hasDetails;

    public PlaceInfoView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public PlaceInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public PlaceInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mwz_place_info_view, this);
        titleImageView = findViewById(R.id.mwz_place_info_image_view);
        titleTextView = findViewById(R.id.mwz_place_info_title);
        subtitleTextView = findViewById(R.id.mwz_place_info_subtitle);
        directionButton = findViewById(R.id.mwz_place_info_direction_button);
        directionButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDirectionClick();
            }
        });
        informationsButton = findViewById(R.id.mwz_place_info_information_button);
        informationsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInformationClick();
            }
        });
        detailsWebView = findViewById(R.id.mwz_place_info_details_webview);

        objectInfoFrameLayout = findViewById(R.id.mwz_place_info_layout);
        objectInfoFrameLayout.setOnClickListener(v -> {
            if (hasDetails) {
                showDetails();
            }
        });
        closeDetailsButton = findViewById(R.id.mwz_place_info_close_details);
        closeDetailsButton.setOnClickListener(v -> {
            hideDetails();
        });
        progressBar = findViewById(R.id.mwz_place_info_loader);
    }

    private void showDetails() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) this.getLayoutParams();
        lp.height = LayoutParams.MATCH_PARENT;
        this.setLayoutParams(lp);
        closeDetailsButton.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
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
    }

    /**
     * Display information about a place
     * @param place to show information
     * @param language use to display text
     */
    public void setContent(Place place, String language) {
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

        if (interactionListener != null && interactionListener.shouldDisplayInformationButton(place)) {
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
        detailsWebView.setVisibility(INVISIBLE);
        titleImageView.setImageDrawable(getContext().getDrawable(R.drawable.ic_location_on_black_24dp));

        informationsButton.setVisibility(View.INVISIBLE);
        //detailsWebView.setVisibility(View.INVISIBLE);
        directionButton.setVisibility(View.INVISIBLE);
        hasDetails = false;
        progressBar.setVisibility(View.VISIBLE);
        objectInfoFrameLayout.setVisibility(View.VISIBLE);
    }

    public void setContentFromPreview(Place place, String language) {
        progressBar.setVisibility(View.GONE);
        if (interactionListener != null && interactionListener.shouldDisplayInformationButton(place)) {
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
    public void setContent(Placelist placelist, String language) {
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
        if (interactionListener != null && interactionListener.shouldDisplayInformationButton(placelist)) {
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

    /**
     * Get the BottomCardListener
     * @return the listener
     */
    public PlaceInfoViewListener getListener() {
        return listener;
    }

    /**
     * Set the PlaceInfoViewListener
     * @param listener the listener
     */
    public void setListener(PlaceInfoViewListener listener) {
        this.listener = listener;
    }

    /**
     * Interface used to listen bottom view event
     */
    public interface PlaceInfoViewListener {

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