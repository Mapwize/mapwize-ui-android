package io.mapwize.mapwizeui.details;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.test.core.app.ApplicationProvider;

import static io.mapwize.mapwizeui.details.OpeningHours.getLabel;
import static org.junit.Assert.assertEquals;

/**
 * This is the third test to test the OpeningHours format for isOpen, opensAt and closesAt
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = {21}, qualifiers = "fr")
public class OpeningHoursTest4 {
    List<Map<String, Object>> daysMock = new ArrayList<>();
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();

        daysMock.add(new HashMap<String, Object>() {{
            put("day", 1);
            put("open", "08:00");
            put("close", "17:00");
        }});
        daysMock.add(new HashMap<String, Object>() {{
            put("day", 5);
            put("open", "07:00");
            put("close", "17:30");
        }});
    }

    @Test
    public void getLabelTest() {
        OpeningHours.TimeInWeek timeInWeek = new OpeningHours.TimeInWeek(3, 12, 54);
        assertEquals("Fermé, ouvre vendredi à 07:00", getLabel(context, daysMock, timeInWeek));
    }

}