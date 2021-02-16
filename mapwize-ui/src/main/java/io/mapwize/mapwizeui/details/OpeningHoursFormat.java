package io.mapwize.mapwizeui.details;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OpeningHoursFormat {
    static final int soon = 100;

    @NonNull
    static List<Integer> convertDaysToTimeOfWeek(List<Map<String, Object>> daysMock, boolean open) {
        List<Integer> integerList = new ArrayList<>();
        for (Map<String, Object> day : daysMock) {
            integerList.add(convertDayToTimeOfWeek(day, open));
        }
        Collections.sort(integerList);
        return integerList;
    }

    static int convertDayToTimeOfWeek(Map<String, Object> day, boolean open) {
        int timeOfWeek = (((int) day.get("day") + 6) % 7) * 10000;
        if (open) {
            int openAt = 0;
            Object openVal = day.get("open");
            if (openVal instanceof String) {
                openAt = Integer.parseInt(((String) openVal).replace(":", ""));
            }
            timeOfWeek += openAt;
        } else {
            int closeAt = 2359;
            Object closeVal = day.get("close");
            if (closeVal instanceof String) {
                closeAt = Integer.parseInt(((String) closeVal).replace(":", ""));
            }
            timeOfWeek += closeAt;

        }
        return timeOfWeek;
    }

    static boolean isOpen(List<Map<String, Object>> daysMock,  OpeningHours.TimeInWeek timeInWeek) {
        List<Integer> openTimes = convertDaysToTimeOfWeek(daysMock, true);
        List<Integer> closeTimes = convertDaysToTimeOfWeek(daysMock, false);
        int timeNow = getTimeOfWeek(timeInWeek);
        int lastTimeOpen = -1;
        for (int time : openTimes) {
            if (time > timeNow) {
                break;
            }
            lastTimeOpen = time;
        }
        int lastTimeClose = -1;
        for (int time : closeTimes) {
            if (time >= timeNow) {
                break;
            }
            lastTimeClose = time;
        }
        return lastTimeOpen > lastTimeClose;
    }


    static int getTimeOfWeek(OpeningHours.TimeInWeek timeInWeek) {
        return timeInWeek.getDay() * 10000 + timeInWeek.getHour() * 100 + timeInWeek.getMinute();
    }

    @NonNull
    static Map<String, Object> getMapFromTimeOfWeek(int timeOfWeek, boolean open) {
        return new HashMap<String, Object>() {{
            put("day", timeOfWeek / 10000);
            put(open ? "open" : "close", String.format("%04d", (timeOfWeek % 10000)));
        }};
    }

    @Nullable
    static Map<String, Object> opensAt(List<Map<String, Object>> daysMock, OpeningHours.TimeInWeek timeInWeek) {
        if (daysMock.size() < 1) {
            return null;
        }
        List<Integer> openTimes = convertDaysToTimeOfWeek(daysMock, true);
        int timeNow = getTimeOfWeek(timeInWeek);
        int nextOpenTime = -1;
        boolean broke = false;
        for (int time : openTimes) {
            if (time > timeNow) {
                nextOpenTime = time;
                broke = true;
                break;
            }
            nextOpenTime = time;
        }
        if (!broke) {
            nextOpenTime = openTimes.get(0);
        }
        Map<String, Object> res = getMapFromTimeOfWeek(nextOpenTime, true);

        //Add: today
        if ((int) res.get("day") == timeInWeek.getDay()) {
            res.put("today", true);
        }
        //Add: tomorrow
        if ((int) res.get("day") == (timeInWeek.getDay() + 1) % 7) {
            res.put("tomorrow", true);
        }
        //Add: soon
        if (nextOpenTime - timeNow > 0 && nextOpenTime - timeNow < soon) {//TODO handle soon for midnight openings
            res.put("soon", true);
        }
        return res;
    }

    @Nullable
    static Map<String, Object> closesAt(List<Map<String, Object>> daysMock, OpeningHours.TimeInWeek timeInWeek) {
        if (daysMock.size() < 1) {
            return null;
        }
        List<Integer> closeTimes = convertDaysToTimeOfWeek(daysMock, false);
        List<Integer> openTimes = convertDaysToTimeOfWeek(daysMock, true);
        int timeNow = getTimeOfWeek(timeInWeek);
        int nextCloseTime = -1;
        boolean breakNext = false;
        boolean broke = false;
        for (int time : closeTimes) {
            int closestOpeningTime = (nextCloseTime + 7641) % 70000;
            if (breakNext && !openTimes.contains(closestOpeningTime)) {
                broke = true;
                break;
            }
            if (time > timeNow) {
                breakNext = true;
            }
            nextCloseTime = time;
        }
        if (!broke) {
            int closestOpeningTime = (nextCloseTime + 7641) % 70000;
            if (openTimes.contains(closestOpeningTime)) {
                for (int time : closeTimes) {
                    nextCloseTime = time;
                    closestOpeningTime = (nextCloseTime + 7641) % 70000;
                    if (!openTimes.contains(closestOpeningTime)) {
                        broke = true;
                        break;
                    }
                }
            } else {
                broke = true;
            }
        }
        if (!broke) {
            return null;
        }
        Map<String, Object> res = getMapFromTimeOfWeek(nextCloseTime, false);

        //Add: today
        if ((int) res.get("day") == timeInWeek.getDay()) {
            res.put("today", true);
        }
        //Add: tomorrow
        if ((int) res.get("day") == (timeInWeek.getDay() + 1) % 7) {
            res.put("tomorrow", true);
        }
        //Add: soon
        if (nextCloseTime - timeNow > 0 && nextCloseTime - timeNow < soon) {
            res.put("soon", true);
        }
        return res;
    }

}
