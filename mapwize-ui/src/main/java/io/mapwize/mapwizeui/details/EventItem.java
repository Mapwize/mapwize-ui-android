package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.widget.LinearLayout;

import io.mapwize.mapwizeui.R;


public class EventItem extends LinearLayout {

    public static final float hourUnit = 26f;
    private final float start;
    private final float dp;
    private final float end;

    public EventItem(Context context, float start, float end) {
        super(context);
        inflate(getContext(), R.layout.mapwize_details_event_even_item, this);

        this.start = start;
        this.end = end;

        this.dp = getResources().getDisplayMetrics().density;

        setBackgroundResource(R.drawable.mapwize_details_radius_top);
    }

    public void setMarginParams() {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
        marginLayoutParams.leftMargin = (int) ((start * hourUnit - 2) * dp);
        marginLayoutParams.width = (int) ((end - start) * hourUnit * dp);
        setLayoutParams(marginLayoutParams);
    }


}
