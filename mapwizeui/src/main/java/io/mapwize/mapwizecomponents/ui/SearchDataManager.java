package io.mapwize.mapwizecomponents.ui;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.api.Place;

/**
 * Helper to preload data and improving search result reactivity
 */
public class SearchDataManager {

    List<MapwizeObject> venuesList;
    List<MapwizeObject> mainSearch;
    List<Place> mainFrom;

    public SearchDataManager() {
        venuesList = new ArrayList<>();
        mainSearch = new ArrayList<>();
        mainFrom = new ArrayList<>();
    }

    public List<MapwizeObject> getVenuesList() {
        return venuesList;
    }

    public void setVenuesList(List<MapwizeObject> venuesList) {
        this.venuesList = venuesList;
    }

    public List<MapwizeObject> getMainSearch() {
        return mainSearch;
    }

    public void setMainSearch(List<MapwizeObject> mainSearch) {
        this.mainSearch = mainSearch;
    }

    public List<Place> getMainFrom() {
        return mainFrom;
    }

    public void setMainFrom(List<Place> mainFrom) {
        this.mainFrom = mainFrom;
    }
}
