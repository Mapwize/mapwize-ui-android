package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageButton;

import io.mapwize.mapwizeui.R;
import io.mapwize.mapwizesdk.map.FollowUserMode;
import io.mapwize.mapwizesdk.map.MapwizeMap;

/**
 * Follow user button allows user to change the follow user mode
 */
public class FollowUserButton extends AppCompatImageButton
    implements MapwizeMap.OnFollowUserModeChangeListener {

    private MapwizeMap mapwizeMap;
    private FollowUserButtonListener listener;
    private int followImageResource = R.drawable.ic_my_location_black_24dp;
    private int followHeadingImageResource = R.drawable.ic_explore_black_24dp;
    private int defaultColor = Color.BLACK;
    private int activeColor = R.color.mapwize_main_color;

    public FollowUserButton(Context context) {
        super(context);
        initialize(context);
    }

    public FollowUserButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FollowUserButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundResource(R.drawable.mapwize_circle_view);
        setImageResource(followImageResource);
        setColorFilter(Color.BLACK);
        setOnClickListener(view -> {
            if (mapwizeMap == null) {
                return;
            }

            if (mapwizeMap.getUserLocation() == null) {
                listener.onFollowUserClickWithoutLocation();
                return;
            }

            switch (mapwizeMap.getFollowUserMode()) {
                case NONE:
                case FOLLOW_USER_AND_HEADING:
                    mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER);
                    break;
                case FOLLOW_USER:
                    mapwizeMap.setFollowUserMode(FollowUserMode.FOLLOW_USER_AND_HEADING);
                    break;
            }
        });
    }

    /**
     * Set the mapwize plugin
     * @param mapwizeMap is used to access mapwize method from this button
     */
    public void setMapwizeMap(@NonNull MapwizeMap mapwizeMap) {
        this.mapwizeMap = mapwizeMap;
        this.mapwizeMap.addOnFollowUserModeChangeListener(this);
    }

    public void setListener(FollowUserButtonListener listener) {
        this.listener = listener;
    }

    /**
     * Called by mapwize when the follow user mode has changed
     * @param followUserMode the new followUserMode
     */
    @Override
    public void onFollowUserModeChange(@NonNull FollowUserMode followUserMode) {
        switch (followUserMode) {
            case NONE:
                setImageResource(followImageResource);
                this.getDrawable().setColorFilter(defaultColor, PorterDuff.Mode.SRC_ATOP);
                break;
            case FOLLOW_USER:
                setImageResource(followImageResource);
                this.getDrawable().setColorFilter(getResources().getColor(activeColor), PorterDuff.Mode.SRC_ATOP);
                break;
            case FOLLOW_USER_AND_HEADING:
                setImageResource(followHeadingImageResource);
                this.getDrawable().setColorFilter(getResources().getColor(activeColor), PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    interface FollowUserButtonListener {
        void onFollowUserClickWithoutLocation();
    }
}
