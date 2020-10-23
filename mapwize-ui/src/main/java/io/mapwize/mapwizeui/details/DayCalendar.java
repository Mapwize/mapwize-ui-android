package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.mapwize.mapwizeui.R;

import static io.mapwize.mapwizeui.details.EventItem.hourUnit;


public class DayCalendar extends ConstraintLayout {
    private double currentHour = 0;
    RelativeLayout eventsList;

    public DayCalendar(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public DayCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public DayCalendar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(getContext(), R.layout.mapwize_details_day_calendar, this);
        eventsList = findViewById(R.id.eventsList);
        final HorizontalScrollView horizontalScrollView = findViewById(R.id.hoursScrollView);
        final View currentTimeBar = findViewById(R.id.currentTimeBar);

        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        currentHour = hour24hrs + minutes / 60f;


        final float dp = getResources().getDisplayMetrics().density;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        horizontalScrollView.post(() -> horizontalScrollView.scrollTo((int) ((currentHour - 1) * hourUnit * dp), 0));

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) currentTimeBar.getLayoutParams();
        marginLayoutParams.leftMargin = (int) ((currentHour * hourUnit) * dp);
        currentTimeBar.setLayoutParams(marginLayoutParams);

        addEvents(context);
    }

    private void addEvents(Context context) {
        String[][] events = {
                {"8", "9", "Waking up", "#EEFF22"},
                {"11", "13", "Point Indoor Analytics", "#FFEE22"},
                {"16", "18", "Point Design", "#EEFF22"},
                {"19", "20", "Proof reading", "#FFEE22"},
        };
        for (String[] event : events) {
            EventItem eventItem = new EventItem(context,
                    Float.parseFloat(event[0]),
                    Float.parseFloat(event[1])
            );
            eventsList.addView(eventItem);
            eventItem.setMarginParams();
        }
    }
}
