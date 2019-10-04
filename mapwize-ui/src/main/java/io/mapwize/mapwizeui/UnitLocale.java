package io.mapwize.mapwizeui;

import java.util.Locale;

/**
 * Helper class to display distance unit depends of the locale
 */
class UnitLocale {

    private static UnitLocale Imperial = new UnitLocale();
    private static UnitLocale Metric = new UnitLocale();

    private static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }

    private static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry();
        if ("US".equals(countryCode)) return Imperial; // USA
        if ("LR".equals(countryCode)) return Imperial; // liberia
        if ("MM".equals(countryCode)) return Imperial; // burma
        return Metric;
    }

    static String distanceAsString(double distance) {

        if (getDefault() == Imperial) {
            long distanceInFeet = Math.round(distance * 3.28084);
            return "" + distanceInFeet + "ft";
        }
        else {
            return "" + Math.round(distance) + "m";
        }

    }

}