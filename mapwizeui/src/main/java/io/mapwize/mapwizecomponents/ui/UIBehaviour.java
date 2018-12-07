package io.mapwize.mapwizecomponents.ui;

import java.util.List;

import io.mapwize.mapwizeformapbox.api.MapwizeObject;

public interface UIBehaviour {
    default boolean shouldDisplayInformationButton(MapwizeObject mapwizeObject) {
        return true;
    }
    default boolean shouldDisplayFloorController(List<Double> floors) {
        return true;
    }
}