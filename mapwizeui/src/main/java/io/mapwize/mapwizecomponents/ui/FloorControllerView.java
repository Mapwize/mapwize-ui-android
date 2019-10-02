package io.mapwize.mapwizecomponents.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizecomponents.R;
import io.mapwize.mapwizeformapbox.api.Floor;
import io.mapwize.mapwizeformapbox.map.MapwizeMap;

/**
 * Floor controller
 */
public class FloorControllerView extends ScrollView implements MapwizeMap.OnFloorChangeListener,
        MapwizeMap.OnFloorsChangeListener {

    private List<Double> directionFloors = new ArrayList<>();
    private LinearLayout linearLayout;
    private int viewSize = 0;
    private MapwizeMap mapwizeMap;
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
     * @param mapwizeMap used to listener floor and floors changed event
     */
    public void setMapwizeMap(@NonNull MapwizeMap mapwizeMap) {
        this.mapwizeMap = mapwizeMap;
        this.mapwizeMap.addOnFloorsChangeListener(this);
        this.mapwizeMap.addOnFloorChangeListener(this);
    }


    /**
     * Called by mapwize when the list of available floors changed
     * @param floors the new available floors
     */
    @Override
    public void onFloorsChange(@NonNull List<Floor> floors) {
        linearLayout.removeAllViews();
        if (!fragmentInteractionListener.shouldDisplayFloorController(floors)) {
            return;
        }
        for (Floor floor : floors) {
            TextView b = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    viewSize,viewSize
            );
            params.setMargins(0,5,0,5);
            b.setElevation(4);
            b.setLayoutParams(params);
            if (floor.getNumber()%1>0) {
                b.setText(String.valueOf(floor.getNumber()));
            }
            else {
                b.setText(String.valueOf(Math.round(floor.getNumber())));
            }
            b.setGravity(Gravity.CENTER);
            b.setBackgroundResource(R.drawable.rounded_button);
            b.setOnClickListener(v -> {
                TextView tv = (TextView)v;
                Double selectedFloor = Double.parseDouble(tv.getText().toString());
                mapwizeMap.setFloor(selectedFloor);
            });
            linearLayout.addView(b);
        }

        this.onFloorChange(mapwizeMap.getFloor());
    }

    @Override
    public void onFloorWillChange(@Nullable Floor floor) {

    }

    @Override
    public void onFloorChange(@Nullable Floor floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            TextView tv  = (TextView) linearLayout.getChildAt(i);
            Double tvValue = Double.parseDouble(tv.getText().toString());
            if (floor != null && floor.getNumber().equals(tvValue)) {
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
}