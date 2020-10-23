package io.mapwize.mapwizeui.details;

import android.content.Context;

import androidx.annotation.NonNull;
import io.mapwize.mapwizeui.R;


public class Occupancy extends Row {
    public Occupancy(@NonNull Context context, String label, int icon, boolean available, int rowType, OnClickListener clickListener) {
        super(context, label, icon, available, rowType, clickListener);
    }

    @Override
    public int getRowType() {
        return rowType;
    }

    @Override
    void initLayout(final Context context, String label, int iconId, boolean highlighted, int rowType, OnClickListener clickListener) {
        inflate(context, R.layout.mapwize_details_occupancy, this);
        this.context = context;
        this.rowType = rowType;
        rowLabel = findViewById(R.id.rowLabel);
        DayCalendar dayCalendar = findViewById(R.id.day_calendar);
        iconImageView = findViewById(R.id.rowIcon);
        this.setOnClickListener(clickListener);
        rowLabel.setText(label);
        this.iconId = iconId;
        iconImageView.setImageResource(iconId);
        setAvailability(highlighted);
        if (highlighted) {
            dayCalendar.setVisibility(VISIBLE);
        }
    }
}
