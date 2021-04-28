package io.mapwize.mapwizeui.details;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.mapwize.mapwizeui.details.DayCalendar.adaptToday;
import static io.mapwize.mapwizeui.details.DayCalendar.atEndOfDay;
import static io.mapwize.mapwizeui.details.DayCalendar.atStartOfDay;
import static io.mapwize.mapwizeui.details.DayCalendar.getMinuteInDay;
import static io.mapwize.mapwizeui.details.DayCalendar.occupied;
import static io.mapwize.mapwizeui.details.DayCalendar.parseDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DayCalendarOccupancyTest {
    Date now = parseDate("2020-11-16T15:20:00.000Z");

    @Test
    public void adaptMinuteTest() {
        assertEquals(1165, getMinuteInDay(parseDate("2020-11-16T19:25:00.000Z")));
        assertEquals(20, getMinuteInDay(parseDate("2020-11-16T00:20:00.000Z")));
    }

    @Test
    public void adaptTodayTest() {
        Date startOfDay = atStartOfDay(now);
        Date endOfDay = atEndOfDay(now);

        Map<String, Object> event = new HashMap<String, Object>() {{
            put("subject", "Small event");
            put("start", "2020-11-16T08:20:00.000Z");
            put("end", "2020-11-16T09:00:00.000Z");
            put("id", "eventId1");
        }};
        assertDay(500, 540, adaptToday(event, startOfDay, endOfDay), event);
        assertFalse(occupied(event, now));

        event = new HashMap<String, Object>() {{
            put("subject", "Whole day");
            put("start", "2020-11-15T11:20:00.000Z");
            put("end", "2020-11-17T08:00:00.000Z");
            put("id", "eventId2");
        }};
        assertDay(0, 1439, adaptToday(event, startOfDay, endOfDay), event);
        assertTrue(occupied(event, now));

        event = new HashMap<String, Object>() {{
            put("subject", "multiple days left");
            put("start", "2020-11-15T11:20:00.000Z");
            put("end", "2020-11-16T08:00:00.000Z");
            put("id", "eventId3");
        }};
        assertDay(0, 480, adaptToday(event, startOfDay, endOfDay), event);
        assertFalse(occupied(event, now));

        event = new HashMap<String, Object>() {{
            put("subject", "multiple days right");
            put("start", "2020-11-16T11:20:00.000Z");
            put("end", "2020-11-22T08:00:00.000Z");
            put("id", "eventId4");
        }};
        assertDay(680, 1439, adaptToday(event, startOfDay, endOfDay), event);
        assertTrue(occupied(event, now));

        event = new HashMap<String, Object>() {{
            put("subject", "Not today");
            put("start", "2020-11-19T11:20:00.000Z");
            put("end", "2020-11-22T08:00:00.000Z");
            put("id", "eventId5");
        }};
        assertNull(adaptToday(event, startOfDay, endOfDay));

        event = new HashMap<String, Object>() {{
            put("subject", "Not today");
            put("start", "2020-11-14T11:20:00.000Z");
            put("end", "2020-11-15T08:00:00.000Z");
            put("id", "eventId5");
        }};
        assertNull(adaptToday(event, startOfDay, endOfDay));
    }

    private void assertDay(int start, int end, Map<String, Object> adaptedEvent, Map<String, Object> original) {
        assertEquals(original.get("id"), adaptedEvent.get("id"));
        assertEquals(original.get("subject"), adaptedEvent.get("subject"));
        assertEquals(start, adaptedEvent.get("start"));
        assertEquals(end, adaptedEvent.get("end"));
    }


}