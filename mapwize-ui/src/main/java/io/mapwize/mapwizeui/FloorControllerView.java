package io.mapwize.mapwizeui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.map.MapwizeMap;

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
        viewSize = (int)getContext().getResources().getDimension(R.dimen.mapwize_ui_floor_button_size);
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
        List<Floor> reversedFloor = new ArrayList<>(floors);
        Collections.reverse(reversedFloor);
        for (Floor floor : reversedFloor) {
            FloorView floorView = new FloorView(getContext(), floor);
            //TextView b = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    viewSize,viewSize
            );
            params.setMargins(0,5,0,5);
            floorView.setElevation(4);
            floorView.setLayoutParams(params);
            //b.setText(floor.getName());
            //b.setGravity(Gravity.CENTER);
            floorView.setBackgroundResource(R.drawable.rounded_button);
            floorView.setOnClickListener(v -> {
                FloorView tv = (FloorView) v;
                Double selectedFloor = tv.getFloor().getNumber();
                mapwizeMap.setFloor(selectedFloor);
            });
            linearLayout.addView(floorView);
        }

        this.onFloorChange(mapwizeMap.getFloor());
    }

    @Override
    public void onFloorWillChange(@Nullable Floor floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            FloorView tv  = (FloorView) linearLayout.getChildAt(i);
            Double tvValue = tv.getFloor().getNumber();
            if (floor != null && floor.getNumber().equals(tvValue)) {
                tv.setLoading();
            }
            else {
                tv.setSelected(false);
            }
        }
    }

    @Override
    public void onFloorChange(@Nullable Floor floor) {
        for (int i = 0; i< linearLayout.getChildCount(); i++) {
            FloorView tv  = (FloorView) linearLayout.getChildAt(i);
            Double tvValue = tv.getFloor().getNumber();
            if (floor != null && floor.getNumber().equals(tvValue)) {
                tv.setSelected(true);
            }
            else {
                tv.setSelected(false);
            }
        }
    }
}