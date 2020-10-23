package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mapwize.mapwizeui.R;


public class Row extends LinearLayout {
    public static final int OTHER = -1;
    public static final int FLOOR_ROW = 0;
    public static final int OPENING_TIME_ROW = 1;
    public static final int PHONE_NUMBER_ROW = 2;
    public static final int WEBSITE_ROW = 3;
    public static final int CAPACITY_ROW = 4;
    public static final int OCCUPANCY_ROW = 5;
    ImageView iconImageView;
    Context context;
    TextView rowLabel;
    int iconId;
    int rowType = OTHER;
    private boolean available;

    public Row(@NonNull Context context, String label, int icon, boolean available, int rowType, OnClickListener clickListener) {
        super(context);
        initLayout(context, label, icon, available, rowType, clickListener);
    }

    public Row(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, "03 59 08 32 30ic_baseline_call_24ic_baseline_call_24ic_baseline_call_24", R.drawable.mapwize_details_ic_baseline_call_24, true, OTHER, view -> {
        });
    }

    public Row(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, "03 59 08 32 30ic_baseline_call_24ic_baselineic_baseline_call_24_call_24", R.drawable.mapwize_details_ic_baseline_call_24, true, OTHER, view -> {
        });
    }

    public int getRowType() {
        return rowType;
    }

    void initLayout(final Context context, String label, int iconId, boolean available, int rowType, OnClickListener clickListener) {
        inflate(context, R.layout.mapwize_details_row, this);
        this.context = context;
        this.rowType = rowType;
        this.available = available;
        this.iconId = iconId;
        rowLabel = findViewById(R.id.rowLabel);
        iconImageView = findViewById(R.id.rowIcon);
        if (clickListener != null) {
            setOnClickListener(clickListener);
        }
        rowLabel.setText(label);
        iconImageView.setImageResource(iconId);
        setAvailability(available);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean available) {
        this.available = available;
        if (available) {
            iconImageView.clearColorFilter();
            rowLabel.setTypeface(rowLabel.getTypeface(), Typeface.NORMAL);
            rowLabel.setTextColor(getResources().getColor(R.color.mapwize_details_row_unavailable_text_color));
            rowLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            iconImageView.setColorFilter(Color.GRAY);
            rowLabel.setTypeface(rowLabel.getTypeface(), Typeface.ITALIC);
            rowLabel.setTextColor(getResources().getColor(R.color.mapwize_details_row_text_color));
            rowLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            rowLabel.setText(getNonAvailableLabel());
        }
    }

    public String getNonAvailableLabel() {
        switch (rowType) {
            case CAPACITY_ROW:
                return "Capacity is not available";
            case WEBSITE_ROW:
                return "Website is not available";
            case OCCUPANCY_ROW:
                return "Occupation is not available";
            case PHONE_NUMBER_ROW:
                return "Phone number is not available";
            case OPENING_TIME_ROW:
                return "Opening hours are not available";
        }
        return "Information not available";
    }

}
