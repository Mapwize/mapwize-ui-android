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
    private ColorStateList oldTvColors;

    public FloorView(@NonNull Context context, @NonNull Floor floor) {
        super(context);
        this.floor = floor;
        this.initialize(context);
    }

    private void initialize(@NonNull Context context) {
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
        if (selected) {
            setBackground(getResources().getDrawable(R.drawable.mapwize_custom_floor_view_selected));
            textView.setTextColor(Color.WHITE);
        }
        else {
            setBackground(getResources().getDrawable(R.drawable.mapwize_custom_floor_view));
            textView.setTextColor(oldTvColors);
        }
    }
}
