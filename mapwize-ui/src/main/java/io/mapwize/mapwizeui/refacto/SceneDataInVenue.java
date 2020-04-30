package io.mapwize.mapwizeui.refacto;

import java.util.ArrayList;
import java.util.List;

import io.mapwize.mapwizesdk.api.Floor;
import io.mapwize.mapwizesdk.api.MapwizeObject;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

public class SceneDataInVenue {

    String language;
    MapwizeObject selectedContent;
    Venue venue;
    Universe universe;
    Floor floor;
    List<Floor> floors;
    List<Universe> universes;
    String venueLanguage;
    List<String> venueLanguages;

    private SceneDataInVenue(Builder builder) {
        this.language = builder.language;
        this.selectedContent = builder.selectedContent;
        this.venue = builder.venue;
        this.universe = builder.universe;
        this.floor = builder.floor;
        this.floors = builder.floors;
        this.universes = builder.universes;
        this.venueLanguage = builder.venueLanguage;
        this.venueLanguages = builder.venueLanguages;
    }

    public static class Builder {

        String language = "en";
        MapwizeObject selectedContent = null;
        Venue venue = null;
        Universe universe = null;
        Floor floor = null;
        List<Floor> floors = new ArrayList<>();
        List<Universe> universes = new ArrayList<>();
        String venueLanguage = "en";
        List<String> venueLanguages = new ArrayList<>();

        Builder language(String language) {
            this.language = language;
            return this;
        }

        Builder selectedContent(MapwizeObject selectedContent) {
            this.selectedContent = selectedContent;
            return this;
        }

        Builder venue(Venue venue) {
            this.venue = venue;
            return this;
        }

        Builder universe(Universe universe) {
            this.universe = universe;
            return this;
        }

        Builder floor(Floor floor) {
            this.floor = floor;
            return this;
        }

        Builder floors(List<Floor> floors) {
            this.floors = floors;
            return this;
        }

        Builder universes(List<Universe> universes) {
            this.universes = universes;
            return this;
        }

        Builder venueLanguage(String venueLanguage) {
            this.venueLanguage = venueLanguage;
            return this;
        }

        Builder venueLanguages(List<String> venueLanguages) {
            this.venueLanguages = venueLanguages;
            return this;
        }

        SceneDataInVenue build() {
            return new SceneDataInVenue(this);
        }
    }

}
