package io.mapwize.mapwizeui;

import java.util.List;

import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Universe;

public class SearchResponse {

    private Universe universe;
    private List<MapwizeObject> results;

    public SearchResponse(Universe universe, List<MapwizeObject> results) {
        this.universe = universe;
        this.results = results;
    }

    public Universe getUniverse() {
        return universe;
    }

    public List<MapwizeObject> getResults() {
        return results;
    }

    public void setResults(List<MapwizeObject> results) {
        this.results = results;
    }
}
