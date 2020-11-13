package io.mapwize.mapwizeui.details;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mapwize.mapwizeui.details.OpeningHours.TimeInWeek;

import static io.mapwize.mapwizeui.details.OpeningHoursFormat.closesAt;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.convertDayToTimeOfWeek;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.convertDaysToTimeOfWeek;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.getMapFromTimeOfWeek;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.getTimeOfWeek;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.isOpen;
import static io.mapwize.mapwizeui.details.OpeningHoursFormat.opensAt;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This is the second test to test the OpeningHours format for isOpen, opensAt and closesAt
 */
public class OpeningHoursTest2 {
    List<Map<String, Object>> daysMock = new ArrayList<>();

    @Before
    public void populateMock() {
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "0900");
            put("close", "2359");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0000");
            put("close", "1200");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 2);
            put("open", "0900");
            put("close", "1700");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 3);
            put("open", "0900");
            put("close", "1700");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 4);
            put("open", "0900");
            put("close", "2359");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 5);
            put("open", "0000");
            put("close", "2359");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 6);
            put("open", "0000");
            put("close", "2359");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "1400");
            put("close", "1800");
        }});
    }

    @Test
    public void isOpenTest() {
        assertFalse(isOpen(daysMock, new TimeInWeek(1, 13, 59)));
        assertTrue(isOpen(daysMock, new TimeInWeek(1, 14, 0)));
        assertTrue(isOpen(daysMock, new TimeInWeek(1, 12, 0)));
        assertFalse(isOpen(daysMock, new TimeInWeek(1, 12, 1)));
        assertTrue(isOpen(daysMock, new TimeInWeek(5, 23, 58)));
        assertTrue(isOpen(daysMock, new TimeInWeek(5, 23, 59)));
        assertTrue(isOpen(daysMock, new TimeInWeek(6, 0, 0)));
        assertTrue(isOpen(daysMock, new TimeInWeek(5, 0, 0)));
        assertFalse(isOpen(daysMock, new TimeInWeek(4, 0, 0)));
        assertFalse(isOpen(daysMock, new TimeInWeek(0, 0, 0)));
        assertTrue(isOpen(daysMock, new TimeInWeek(2, 9, 58)));
        assertFalse(isOpen(daysMock, new TimeInWeek(2, 7, 13)));
        assertTrue(isOpen(daysMock, new TimeInWeek(1, 8, 58)));
        assertTrue(isOpen(daysMock, new TimeInWeek(2, 16, 45)));
        assertTrue(isOpen(daysMock, new TimeInWeek(6, 11, 45)));
        assertFalse(isOpen(daysMock, new TimeInWeek(1, 13, 35)));
        assertTrue(isOpen(daysMock, new TimeInWeek(0, 13, 35)));
    }

    @Test
    public void opensAtTest() {
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "1400");
            put("soon", true);
            put("today", true);
        }}, opensAt(daysMock, new TimeInWeek(1, 13, 35)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "0900");
            put("tomorrow", true);
        }}, opensAt(daysMock, new TimeInWeek(6, 11, 45)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("open", "0900");
            put("today", true);
        }}, opensAt(daysMock, new TimeInWeek(2, 7, 13)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("open", "0900");
            put("soon", true);
            put("today", true);
        }}, opensAt(daysMock, new TimeInWeek(2, 8, 58)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 3);
            put("open", "0900");
            put("tomorrow", true);
        }}, opensAt(daysMock, new TimeInWeek(2, 9, 58)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 3);
            put("open", "0900");
            put("tomorrow", true);
        }}, opensAt(daysMock, new TimeInWeek(2, 16, 45)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0000");
            put("tomorrow", true);
        }}, opensAt(daysMock, new TimeInWeek(0, 13, 35)));
    }

    @Test
    public void closesAtTest() {
        assertEquals(new HashMap<String, Object>() {{
            put("day", 6);
            put("close", "2359");
        }}, closesAt(daysMock, new TimeInWeek(4, 13, 35)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("close", "1200");
            put("tomorrow", true);
        }}, closesAt(daysMock, new TimeInWeek(0, 13, 35)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("close", "1700");
            put("tomorrow", true);
        }}, closesAt(daysMock, new TimeInWeek(1, 18, 35)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 6);
            put("close", "2359");
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(6, 11, 45)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("close", "1700");
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(2, 7, 13)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("close", "1700");
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(2, 8, 58)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("close", "1700");
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(2, 9, 58)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 2);
            put("close", "1700");
            put("soon", true);
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(2, 16, 45)));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("close", "1800");
            put("today", true);
        }}, closesAt(daysMock, new TimeInWeek(1, 13, 35)));
    }

    @Test
    public void convertDayToTimeOfWeekTest() {
        assertEquals(10900, convertDayToTimeOfWeek(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0900");
            put("close", "1200");
        }}, true));
        assertEquals(900, convertDayToTimeOfWeek(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "0900");
            put("close", "1200");
        }}, true));
        assertEquals(1200, convertDayToTimeOfWeek(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "0900");
            put("close", "1200");
        }}, false));
        assertEquals(21345, convertDayToTimeOfWeek(new HashMap<String, Object>() {{
            put("day", 2);
            put("open", "1345");
            put("close", "1200");
        }}, true));
    }

    @Test
    public void convertDaysToTimeOfWeekTest() {
        assertThat(convertDaysToTimeOfWeek(daysMock, true), is(Arrays.asList(900, 10000, 11400, 20900, 30900, 40900, 50000, 60000)));
        assertThat(convertDaysToTimeOfWeek(daysMock, false), is(Arrays.asList(2359, 11200, 11800, 21700, 31700, 42359, 52359, 62359)));
    }

    @Test
    public void getTimeOfWeekTest() {
        assertEquals(12329, getTimeOfWeek(new TimeInWeek(1, 23, 29)));
        assertEquals(103, getTimeOfWeek(new TimeInWeek(0, 1, 3)));
        assertEquals(61354, getTimeOfWeek(new TimeInWeek(6, 13, 54)));
        assertEquals(30804, getTimeOfWeek(new TimeInWeek(3, 8, 4)));
    }

    @Test
    public void getMapFromTimeOfWeekTest() {
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "2329");
        }}, getMapFromTimeOfWeek(12329, true));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0329");
        }}, getMapFromTimeOfWeek(10329, true));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0309");
        }}, getMapFromTimeOfWeek(10309, true));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 0);
            put("open", "0000");
        }}, getMapFromTimeOfWeek(0, true));
        assertEquals(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "0000");
        }}, getMapFromTimeOfWeek(10000, true));
    }

}