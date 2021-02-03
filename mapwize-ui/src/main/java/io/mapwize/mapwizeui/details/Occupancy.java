package io.mapwize.mapwizeui.details;

import android.content.Context;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.mapwize.mapwizeui.R;


public class Occupancy extends Row {

    private final List<Map<String, Object>> events;
    DayCalendar dayCalendar;

    public Occupancy(@NonNull Context context, String label, List<Map<String, Object>> events, int icon, boolean available, int rowType, OnClickListener clickListener) {
        super(context, label, icon, available, rowType, clickListener);
        this.events = events;
        if (available && dayCalendar != null) {
            boolean occupied = dayCalendar.setEvents(events, available);
            String calculatedLabel = occupied ? context.getString(R.string.mapwize_details_occupied) : context.getString(R.string.mapwize_details_not_occupied);
            rowLabel.setText(calculatedLabel);
        }
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
        dayCalendar = findViewById(R.id.day_calendar);
        iconImageView = findViewById(R.id.rowIcon);
        this.setOnClickListener(clickListener);
        rowLabel.setText(label);
        this.iconId = iconId;
        iconImageView.setImageResource(iconId);
        setAvailability(highlighted);
    }
}
