package io.mapwize.mapwizeui.details;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.mapwize.mapwizeui.R;


public class Occupancy extends Row {

    private final List<Map<String, Object>> events;
    DayCalendar dayCalendar;

    public Occupancy(@NonNull Context context, String label, List<Map<String, Object>> events, int icon, boolean available, int rowType, Date now, OnClickListener clickListener) {
        super(context, label, icon, available, rowType, clickListener);
        this.events = events;
        if (available && dayCalendar != null) {
            dayCalendar.setEvents(events, available);
            String calculatedLabel = Occupancy.getOccupiedLabel(events, now, context);
            rowLabel.setText(calculatedLabel);
        }
    }

    public static String getOccupiedLabel(List<Map<String, Object>> events, Date now, Context context) {
        boolean occupied = DayCalendar.isOccupied(events, now);
        return occupied ? context.getString(R.string.mapwize_details_occupied) : context.getString(R.string.mapwize_details_not_occupied);
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
