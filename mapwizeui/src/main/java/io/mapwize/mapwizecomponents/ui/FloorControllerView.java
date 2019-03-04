package io.mapwize.mapwizecomponents.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.mapwize.mapwizeformapbox.api.Venue;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

/**
 * Floor controller
 */
public class FloorControllerView extends RecyclerView implements MapwizePlugin.OnFloorChangeListener,
        MapwizePlugin.OnFloorsChangeListener, MapwizePlugin.OnVenueEnterListener {

    private MapwizePlugin mapwizePlugin;
    private FloorControllerAdapter adapter;
    private Venue venue;
    private List<Floor> floors;

    public FloorControllerView(@NonNull Context context) {
        super(context);
        initComponent(context);
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponent(context);
    }

    public FloorControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initComponent(context);
    }

    private void initComponent(Context context) {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        this.adapter = new FloorControllerAdapter();
        this.adapter.setListener(floor -> mapwizePlugin.setFloor(floor.getRawValue()));
        this.setAdapter(adapter);

    }

    /**
     * Set mapwize plugin
     * @param mapwizePlugin used to listener floor and floors changed event
     */
    public void setMapwizePlugin(@NonNull MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        this.mapwizePlugin.addOnFloorsChangeListener(this);
        this.mapwizePlugin.addOnFloorChangeListener(this);
        this.mapwizePlugin.addOnVenueEnterListener(this);
    }

    /**
     * Called by mapwize when the current floor has changed
     * @param floor the new current floor
     */
    @Override
    public void onFloorChange(@Nullable Double floor) {
        for (Floor f : floors) {
            if (f.getRawValue().equals(floor)) {
                f.setSelected(true);
            }
            else {
                f.setSelected(false);
            }
        }
        adapter.swapData(floors);
        /*for (int i = 0; i< linearLayout.getChildCount(); i++) {
            TextView tv  = (TextView) linearLayout.getChildAt(i);
            Double tvValue = Double.parseDouble(tv.getText().toString());
            if (floor != null && floor.equals(tvValue)) {
                tv.setBackgroundResource(R.drawable.mapwize_floor_controller_selected_floor);
            }
            else {
                tv.setBackgroundResource(R.drawable.rounded_button);
            }
        }*/
    }

    /**
     * Called by mapwize when the list of available floors changed
     * @param floors the new available floors
     */
    @Override
    public void onFloorsChange(@NonNull List<Double> floors) {
        this.floors = new ArrayList<>();

        JSONObject floorRef = null;
        if (venue != null && venue.getData() != null && venue.getData().optJSONObject("floorDisplayMobile") != null) {
            floorRef = venue.getData().optJSONObject("floorDisplayMobile");
        }

        for (Double floor : floors) {
            String k = "";
            if (floor%1>0) {
                k = String.valueOf(floor);
            }
            else {
                k = String.valueOf(Math.round(floor));
            }
            if (floorRef != null) {
                this.floors.add(new Floor(floor, floorRef.optString(k, k)));
            }
            else {
                this.floors.add(new Floor(floor,k));
            }
        }
        adapter.swapData(this.floors);
        this.onFloorChange(mapwizePlugin.getFloor());

        /*linearLayout.removeAllViews();
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

        */
    }

    @Override
    public void onVenueEnter(@NonNull Venue venue) {
    }

    @Override
    public void willEnterInVenue(@NonNull Venue venue) {
        this.venue = venue;
    }
}