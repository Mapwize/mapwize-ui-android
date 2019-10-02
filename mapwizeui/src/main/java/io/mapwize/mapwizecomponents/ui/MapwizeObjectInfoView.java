package io.mapwize.mapwizecomponents.ui;

import io.mapwize.mapwizeformapbox.api.Place;
import io.mapwize.mapwizeformapbox.api.Placelist;
import io.mapwize.mapwizeformapbox.api.Venue;

public interface MapwizeObjectInfoView {

    void setContent(Place place, String language);
    void setContent(Placelist placelist, String language);
    void setContent(Venue venue, String language);
    void removeContent();

}
