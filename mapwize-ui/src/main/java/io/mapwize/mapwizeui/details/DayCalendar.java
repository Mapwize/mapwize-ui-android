package io.mapwize.mapwizeui.details;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.mapwize.mapwizeui.R;

import static io.mapwize.mapwizeui.details.EventItem.hourUnit;


public class DayCalendar extends ConstraintLayout {
    private static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    RelativeLayout eventsList;
    private double currentHour = 0;
    private Context context;
    private HorizontalScrollView horizontalScrollView;
    private View currentTimeBar;

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

    static List<Map<String, Object>> filterAndAdaptToToday(List<Map<String, Object>> events, Date now) {
        List<Map<String, Object>> filteredEvents = new ArrayList<>();
        Date startOfDay = atStartOfDay(now);
        Date endOfDay = atEndOfDay(now);
        for (Map<String, Object> event : events) {
            Map<String, Object> adaptedEvent = adaptToday(event, startOfDay, endOfDay);
            if (adaptedEvent != null) {
                filteredEvents.add(adaptedEvent);
            }
        }
        return filteredEvents;
    }

    static Map<String, Object> adaptToday(Map<String, Object> eventIn, Date startOfDay, Date endOfDay) {
        Map<String, Object> event = new HashMap<>(eventIn);
        Date startDate = parseDate((String) event.get("start"));
        Date endDate = parseDate((String) event.get("end"));
        if (startDate == null || endDate == null) return event;
        if (
                startDate.compareTo(startOfDay) >= 0 &&
                        startDate.compareTo(endOfDay) <= 0 &&
                        endDate.compareTo(endOfDay) <= 0
        ) {//Small Event
            event.put("start", getMinuteInDay(startDate));
            event.put("end", getMinuteInDay(endDate));
            return event;
        } else if (
                startDate.compareTo(startOfDay) <= 0 &&
                        endDate.compareTo(endOfDay) >= 0
        ) {//Whole day
            event.put("start", getMinuteInDay(startOfDay));
            event.put("end", getMinuteInDay(endOfDay));
            return event;
        } else if (
                startDate.compareTo(startOfDay) <= 0 &&
                        endDate.compareTo(startOfDay) >= 0 &&
                        endDate.compareTo(endOfDay) <= 0
        ) {//multiple days left
            event.put("start", getMinuteInDay(startOfDay));
            event.put("end", getMinuteInDay(endDate));
            return event;
        } else if (
                startDate.compareTo(startOfDay) >= 0 &&
                        startDate.compareTo(endOfDay) <= 0 &&
                        endDate.compareTo(endOfDay) >= 0
        ) {//multiple days right
            event.put("start", getMinuteInDay(startDate));
            event.put("end", getMinuteInDay(endOfDay));
            return event;
        }
        return null;
    }

    static Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    static Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static Date parseDate(String dateStr) {
        try {
            parser.setTimeZone(TimeZone.getTimeZone("GMT"));
            return parser.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static int getMinuteInDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    public static boolean occupied(Map<String, Object> adaptedEvent, Date now) {
        int nowInMinute = getMinuteInDay(now);
        return (Integer) adaptedEvent.get("start") < nowInMinute && (Integer) adaptedEvent.get("end") > nowInMinute;
    }

    public static boolean isOccupied(List<Map<String, Object>> events, Date now) {
        List<Map<String, Object>> adaptedEvents = filterAndAdaptToToday(events, now);
        for (Map<String, Object> adaptedEvent : adaptedEvents) {
            if (occupied(adaptedEvent, now)) {
                return true;
            }
        }
        return false;
    }

    private void initLayout(Context context) {
        this.context = context;
        View.inflate(getContext(), R.layout.mapwize_details_day_calendar, this);
        eventsList = findViewById(R.id.eventsList);
        horizontalScrollView = findViewById(R.id.hoursScrollView);
        currentTimeBar = findViewById(R.id.currentTimeBar);
    }

    public void setCurrentTime(Calendar calendar) {
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        currentHour = hour24hrs + minutes / 60f;


        final float dp = getResources().getDisplayMetrics().density;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        horizontalScrollView.post(() -> horizontalScrollView.scrollTo((int) ((currentHour - 2) * hourUnit * dp), 0));

        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) currentTimeBar.getLayoutParams();
        marginLayoutParams.leftMargin = (int) (((currentHour - 1) * hourUnit - 2) * dp);
        currentTimeBar.setLayoutParams(marginLayoutParams);
    }

    public void setEvents(List<Map<String, Object>> events, boolean available, Date now) {
        setVisibility(available);
        List<Map<String, Object>> adaptedEvents = filterAndAdaptToToday(events, now);
        for (Map<String, Object> event : adaptedEvents) {
            EventItem eventItem = new EventItem(context,
                    (float) ((Integer) (event.get("start")) / 60.0) - 1,
                    (float) ((Integer) (event.get("end")) / 60.0) - 1
            );
            eventsList.addView(eventItem);
            eventItem.setMarginParams();
        }
    }

    void setVisibility(boolean visible) {
        if (visible) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

}
