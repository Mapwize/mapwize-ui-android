package io.mapwize.mapwizeui.details;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.test.core.app.ApplicationProvider;

import static io.mapwize.mapwizeui.details.OpeningHours.changeTimezoneOfDate;
import static io.mapwize.mapwizeui.details.OpeningHours.getDate;
import static io.mapwize.mapwizeui.details.OpeningHours.getLabel;
import static io.mapwize.mapwizeui.details.OpeningHours.getTimeInWeek;
import static org.junit.Assert.assertEquals;

/**
 * This is the third test to test the OpeningHours format for isOpen, opensAt and closesAt
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = {21}, qualifiers = "fr")
public class OpeningHoursTest3 {
    List<Map<String, Object>> daysMock = new ArrayList<>();
    private Context context;
    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();

        daysMock.add(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "09:00");
            put("close", "23:59");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "00:00");
            put("close", "12:00");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 2);
            put("open", "09:00");
            put("close", "17:00");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 3);
            put("open", "09:00");
            put("close", "17:00");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 4);
            put("open", "09:00");
            put("close", "23:59");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 5);
            put("open", "00:00");
            put("close", "23:59");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 6);
            put("open", "00:00");
            put("close", "23:59");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "14:00");
            put("close", "18:00");
        }});
    }


    @Test
    public void getDateTest() throws ParseException {
        OpeningHours.TimeInWeek minuteInWeek = new OpeningHours.TimeInWeek(1, 13, 54);
        assertEquals(minuteInWeek, getTimeInWeek(getDate(minuteInWeek)));
    }
    @Test
    public void getLabelTest() throws ParseException {

        OpeningHours.TimeInWeek timeInWeek = new OpeningHours.TimeInWeek(0, 4, 54);
        assertEquals("Fermé, ouvre à 09:00", getLabel(context, daysMock, timeInWeek));

    }
    @Test
    public void changeTimezoneOfDateTest() throws ParseException {

        OpeningHours.TimeInWeek timeInWeek1 = new OpeningHours.TimeInWeek(0, 4, 54);
        Date date = getDate(timeInWeek1);
        Calendar calendar = changeTimezoneOfDate(date, TimeZone.getTimeZone("Europe/Paris"), TimeZone.getTimeZone("America/Chicago"));
        OpeningHours.TimeInWeek timeInWeek2 = getTimeInWeek(calendar);
        assertEquals(new OpeningHours.TimeInWeek(6, 21, 54), timeInWeek2);
    }

}