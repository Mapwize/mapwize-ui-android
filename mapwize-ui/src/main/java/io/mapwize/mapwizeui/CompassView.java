package io.mapwize.mapwizeui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Rewritten mapbox compass to make integration easier to the user interface.
 */
public final class CompassView extends AppCompatImageView implements
        Runnable, MapboxMap.OnCameraIdleListener, MapboxMap.OnCameraMoveListener {

    public static final long TIME_WAIT_IDLE = 500;

    private float rotation = 0.0f;
    private boolean fadeCompassViewFacingNorth = true;
    private ViewPropertyAnimatorCompat fadeAnimator;

    private MapboxMap mapboxMap;
    private OnCompassClickListener onCompassClickListener;

    public CompassView(Context context) {
        super(context);
        initialize(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundResource(R.drawable.mapwize_circle_view);
        float screenDensity = context.getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams((int) (48 * screenDensity), (int) (48 * screenDensity));
        setLayoutParams(lp);
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public void setMapboxMap(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.addOnCameraIdleListener(this);
        this.mapboxMap.addOnCameraMoveListener(this);
        this.setOnClickListener(view -> {
            if (onCompassClickListener != null) {
                onCompassClickListener.onClick(this);
            }
            this.mapboxMap.animateCamera(CameraUpdateFactory.bearingTo(0.0), 300);
            this.postDelayed(this, 300);
        });
    }

    public OnCompassClickListener getOnCompassClickListener() {
        return onCompassClickListener;
    }

    public void setOnCompassClickListener(OnCompassClickListener onClickListener) {
        this.onCompassClickListener = onClickListener;
    }

    private void resetAnimation() {
        if (fadeAnimator != null) {
            fadeAnimator.cancel();
        }
        fadeAnimator = null;
    }

    public boolean isHidden() {
        return fadeCompassViewFacingNorth && isFacingNorth();
    }

    public boolean isFacingNorth() {
        return Math.abs(rotation) >= 359.0 || Math.abs(rotation) <= 1.0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled && !isHidden()) {
            resetAnimation();
            setAlpha(1.0f);
            setVisibility(View.VISIBLE);
        } else {
            resetAnimation();
            setAlpha(0.0f);
            setVisibility(View.GONE);
        }
    }

    /**
     * Updates the direction of the compass.
     *
     * @param bearing the direction value of the map
     */
    public void update(final double bearing) {
        rotation = (float) bearing;

        if (!isEnabled()) {
            return;
        }

        if (isHidden()) {
            if (getVisibility() == View.GONE || fadeAnimator != null) {
                return;
            }
            postDelayed(this, TIME_WAIT_IDLE);
            return;
        } else {
            resetAnimation();
            setAlpha(1.0f);
            setVisibility(View.VISIBLE);
        }

        setRotation(-rotation);
    }

    public void fadeCompassViewFacingNorth(boolean compassFadeFacingNorth) {
        fadeCompassViewFacingNorth = compassFadeFacingNorth;
        if (!compassFadeFacingNorth) {
            setVisibility(View.VISIBLE);
        }
        else if (isFacingNorth()) {
            setVisibility(View.GONE);
        }
    }

    public boolean isFadeCompassViewFacingNorth() {
        return fadeCompassViewFacingNorth;
    }

    /**
     * Set the CompassView image.
     *
     * @param compass the drawable to use as compass image
     */
    public void setCompassImage(Drawable compass) {
        setImageDrawable(compass);
    }

    /**
     * Get the current configured CompassView image.
     *
     * @return the drawable used as compass image
     */
    public Drawable getCompassImage() {
        return getDrawable();
    }

    @Override
    public void run() {
        if (isHidden()) {
            setVisibility(View.GONE);
        }

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMove() {
        this.update(mapboxMap.getCameraPosition().bearing);
    }

    public interface OnCompassClickListener {
        void onClick(CompassView compassView);
    }
}