package io.mapwize.mapwizecomponents.ui;

import io.mapwize.mapwizeformapbox.api.Direction;
import io.mapwize.mapwizeformapbox.map.NavigationInfo;

public interface DirectionInfoView {

    void setContent(Direction direction);
    void setContent(NavigationInfo navigationInfo);
    void removeContent();

}
