package io.mapwize.mapwizecomponents.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Floor controller
 */
public class FloorControllerView extends ScrollView implements MapwizePlugin.OnFloorChangeListener,
        MapwizePlugin.OnFloorsChangeListener {

    private List<Double> directionFloors = new ArrayList<>();
    private LinearLayout linearLayout;
    private int viewSize = 0;
    private MapwizePlugin mapwizePlugin;
    private MapwizeFragment.OnFragmentInteractionListener fragmentInteractionListener;

    public FloorControllerView(@NonNull Context context) {
        super(context);
        initLayout();
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    public MapwizeFragment.OnFragmentInteractionListener getFragmentInteractionListener() {
        return fragmentInteractionListener;
    }

    public void setUiBehaviour(MapwizeFragment.OnFragmentInteractionListener fragmentInteractionListener) {
        this.fragmentInteractionListener = fragmentInteractionListener;
    }

    private void initLayout() {
        this.setVerticalScrollBarEnabled(false);
        viewSize = (int)getContext().getResources().getDimension(R.dimen.mapwize_floor_button_size);
        linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                viewSize,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.setVerticalGravity(Gravity.BOTTOM);
        linearLayout.setLayoutTransition(new LayoutTransition());
        setLayoutTransition(new LayoutTransition());
        getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        this.addView(linearLayout);
    }

    /**
     * Set mapwize plugin
     * @param mapwizePlugin used to listener floor and floors changed event
     */
    public void setMapwizePlugin(@NonNull MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        this.mapwizePlugin.addOnFloorsChangeListener(this);
        this.mapwizePlugin.addOnFloorChangeListener(this);
    }

    /**
     * Called by mapwize when the current floor has changed
     * @param floor the new current floor
     */
    @Override
    public void onFloorChange(@Nullable Double floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            TextView tv  = (TextView) linearLayout.getChildAt(i);
            Double tvValue = Double.parseDouble(tv.getText().toString());
            if (floor != null && floor.equals(tvValue)) {
                tv.setBackgroundResource(R.drawable.mapwize_floor_controller_selected_floor);
            }
            else {
                tv.setBackgroundResource(R.drawable.rounded_button);
            }
            if (directionFloors.indexOf(tvValue) != -1) {
                tv.setTextColor(ContextCompat.getColor(this.getContext(), R.color.mapwize_main_color));
            }
            else {
                tv.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * Called by mapwize when the list of available floors changed
     * @param floors the new available floors
     */
    @Override
    public void onFloorsChange(@NonNull List<Double> floors) {
        linearLayout.removeAllViews();
        if (!fragmentInteractionListener.shouldDisplayFloorController(floors)) {
            return;
        }
        for (Double value : floors) {
            TextView b = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    viewSize,viewSize
            );
            params.setMargins(0,5,0,5);
            b.setElevation(4);
            b.setLayoutParams(params);
            if (value%1>0) {
                b.setText(String.valueOf(value));
            }
            else {
                b.setText(String.valueOf(Math.round(value)));
            }
            b.setGravity(Gravity.CENTER);
            b.setBackgroundResource(R.drawable.rounded_button);
            b.setOnClickListener(v -> {
                TextView tv = (TextView)v;
                Double floor = Double.parseDouble(tv.getText().toString());
                mapwizePlugin.setFloor(floor);
            });
            linearLayout.addView(b);
        }

        this.onFloorChange(mapwizePlugin.getFloor());
    }

}