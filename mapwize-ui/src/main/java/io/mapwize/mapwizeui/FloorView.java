package io.mapwize.mapwizeui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.mapwize.mapwizesdk.api.Floor;

public class FloorView extends FrameLayout {

    private Floor floor;
    private TextView textView;
    private View animationView;
    private ObjectAnimator scale;
    private ColorStateList oldTvColors;
    private ObjectAnimator colorAnim;

    public FloorView(@NonNull Context context, @NonNull Floor floor) {
        super(context);
        this.floor = floor;
        this.initialize(context);
    }

    private void initialize(@NonNull Context context) {
        animationView = new View(context);
        animationView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(ContextCompat.getColor(context, io.mapwize.mapwizeformapbox.R.color.mapwize_main_color));
        animationView.setBackground(drawable);
        animationView.setVisibility(INVISIBLE);
        this.addView(animationView);

        textView = new TextView(context);
        oldTvColors = textView.getTextColors();
        textView.setText(floor.getName());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(textView);
    }

    public Floor getFloor() {
        return floor;
    }

    public void setSelected(boolean selected) {
        if (colorAnim != null) {
            colorAnim.end();
        }
        if (selected) {
            animationView.setVisibility(VISIBLE);
            textView.setTextColor(Color.WHITE);
        }
        else {
            animationView.setVisibility(INVISIBLE);
            textView.setTextColor(oldTvColors);
        }
        scale = null;
        colorAnim = null;
    }

    public void setLoading() {
        scale = ObjectAnimator.ofPropertyValuesHolder(animationView,
                PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0f, 1f));
        scale.setDuration(500);
        scale.start();
        colorAnim = ObjectAnimator.ofInt(textView, "textColor",
                oldTvColors.getDefaultColor(), Color.WHITE);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(500);
        colorAnim.start();
        animationView.setVisibility(VISIBLE);
    }
}
