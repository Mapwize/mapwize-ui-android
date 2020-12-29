package io.mapwize.mapwizeui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.api.DirectionMode;
import io.mapwize.mapwizesdk.map.MapwizeIndoorLocation;

public interface MapwizeFragmentDirectionsInterface {

    void showDirectionLoading();
    void showDirectionInfo(Direction direction);
    void showAccessibleDirectionModes(List<DirectionMode> modes);
    void showDirectionError();
    void hideInfo();
    @Nullable
    MapwizeIndoorLocation getUserLocation();
    @NonNull
    List<DirectionMode> getDirectionModes();

    void setDirection(Direction direction);

    void quitDirections();

//    void removeDirection();
//    void stopNavigation();
//    void removeMarkers();
}
