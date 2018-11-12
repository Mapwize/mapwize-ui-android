package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageButton;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.map.FollowUserMode;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Follow user button allows user to change the follow user mode
 */
public class FollowUserButton extends AppCompatImageButton
    implements MapwizePlugin.OnFollowUserModeChange {

    private MapwizePlugin mapwizePlugin;
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
            if (mapwizePlugin == null) {
                return;
            }
            switch (mapwizePlugin.getFollowUserMode()) {
                case FollowUserMode.NONE:
                    mapwizePlugin.setFollowUserMode(FollowUserMode.FOLLOW_USER);
                    break;
                case FollowUserMode.FOLLOW_USER:
                    mapwizePlugin.setFollowUserMode(FollowUserMode.FOLLOW_USER_AND_HEADING);
                    break;
                case FollowUserMode.FOLLOW_USER_AND_HEADING:
                    mapwizePlugin.setFollowUserMode(FollowUserMode.FOLLOW_USER);
                    break;
            }
        });
    }

    /**
     * Set the mapwize plugin
     * @param mapwizePlugin is used to access mapwize method from this button
     */
    public void setMapwizePlugin(@NonNull MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        this.mapwizePlugin.addOnFollowUserModeChangeListener(this);
    }

    /**
     * Called by mapwize when the follow user mode has changed
     * @param followUserMode the new followUserMode
     */
    @Override
    public void followUserModeChange(int followUserMode) {
        switch (followUserMode) {
            case FollowUserMode.NONE:
                setImageResource(followImageResource);
                this.getDrawable().setColorFilter(defaultColor, PorterDuff.Mode.SRC_ATOP);
                break;
            case FollowUserMode.FOLLOW_USER:
                setImageResource(followImageResource);
                this.getDrawable().setColorFilter(getResources().getColor(activeColor), PorterDuff.Mode.SRC_ATOP);
                break;
            case FollowUserMode.FOLLOW_USER_AND_HEADING:
                setImageResource(followHeadingImageResource);
                this.getDrawable().setColorFilter(getResources().getColor(activeColor), PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }
}
