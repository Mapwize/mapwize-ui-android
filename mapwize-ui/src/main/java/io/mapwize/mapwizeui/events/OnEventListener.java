package io.mapwize.mapwizeui.events;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

public interface OnEventListener {
    default void onContentSelect(@NonNull Place place,
                                 @NonNull Universe currentUniverse,
                                 @NonNull Universe searchResultUniverse,
                                 @NonNull Channel channel,
                                 @Nullable String searchQuery) {

    }
    default void onContentSelect(@NonNull Placelist placelist,
                                 @NonNull Universe currentUniverse,
                                 @NonNull Universe searchResultUniverse,
                                 @NonNull Channel channel,
                                 @Nullable String searchQuery) {

    }
    default void onDirectionStart(@NonNull Venue venue, Universe universe, DirectionPoint from, DirectionPoint to, String mode, boolean isNavigation) {

    }
    default void onUiComponentClick(View view, String name) {

    }
}