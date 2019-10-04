package io.mapwize.mapwizeui;

import io.mapwize.mapwizesdk.api.Direction;
import io.mapwize.mapwizesdk.map.NavigationInfo;

public interface DirectionInfoView {

    void setContent(Direction direction);
    void setContent(NavigationInfo navigationInfo);
    void removeContent();

}
