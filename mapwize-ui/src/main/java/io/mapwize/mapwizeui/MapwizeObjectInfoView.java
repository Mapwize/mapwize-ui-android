package io.mapwize.mapwizeui;

import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Venue;

public interface MapwizeObjectInfoView {

    void setContent(Place place, String language);
    void setContent(Placelist placelist, String language);
    void setContent(Venue venue, String language);
    void removeContent();

}
