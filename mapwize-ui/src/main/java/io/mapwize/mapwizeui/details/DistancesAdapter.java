package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import io.mapwize.mapwizeui.R;


public class DistancesAdapter extends BaseAdapter {

    private List<Map<String, Object>> distances = new ArrayList<>();
    private Context context;
    private PlaceDetails.DistanceItemClickListener distanceItemClickListener;

    public DistancesAdapter(@NonNull Context context) {
        this.context = context;
    }

    public void showPlacelist(List<Map<String, Object>> distances, PlaceDetails.DistanceItemClickListener distanceItemClickListener) {
        this.distanceItemClickListener = distanceItemClickListener;
        this.distances = distances;
        notifyDataSetChanged();
    }

    public List<Map<String, Object>> getDistances() {
        return distances;
    }

    @Override
    public int getCount() {
        return distances.size();
    }

    @Override
    public Object getItem(int position) {
        return distances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.mapwize_details_distance, parent, false);
        }
        TextView floorLabel = convertView.findViewById(R.id.floorLabel);
        TextView distanceLabel = convertView.findViewById(R.id.distanceLabel);
        TextView timeLabel = convertView.findViewById(R.id.timeLabel);
        Map<String, Object> distanceInfo = (Map<String, Object>) getItem(position);
        if (distanceInfo != null) {
            floorLabel.setText(String.format(Locale.getDefault(), "%.0f floor", (double) distanceInfo.get("floor")));
            distanceLabel.setText(String.format(Locale.getDefault(), "%.2fm", (double) distanceInfo.get("distance")));
            timeLabel.setText(String.format(Locale.getDefault(), "%.2fmn", (double) distanceInfo.get("traveltime")));
            convertView.setOnClickListener(view ->
                    distanceItemClickListener.onClick((String) distanceInfo.get("placeId"), (String) distanceInfo.get("venueId")));
        }
        return convertView;
    }

}
