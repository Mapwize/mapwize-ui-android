package io.mapwize.mapwizeui.events;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.mapwize.mapwizesdk.api.DirectionPoint;
import io.mapwize.mapwizesdk.api.Place;
import io.mapwize.mapwizesdk.api.Placelist;
import io.mapwize.mapwizesdk.api.Universe;
import io.mapwize.mapwizesdk.api.Venue;

public class EventManager {

    private static EventManager INSTANCE = null;
    private static OnEventListener mOnEventListener;
    private EventManager() {

    }

    public static void configure(OnEventListener onEventListener) {
        mOnEventListener = onEventListener;
    }

    public static EventManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventManager();
        }
        return INSTANCE;
    }

    public void triggerOnContentSelect(@NonNull Place place,
                                       @NonNull Universe currentUniverse,
                                       @NonNull Universe searchResultUniverse,
                                       @NonNull Channel channel,
                                       @Nullable String searchQuery) {
        if (mOnEventListener != null) {
            mOnEventListener.onContentSelect(place, currentUniverse, searchResultUniverse, channel, searchQuery);
        }
    }

    public void triggerOnContentSelect(@NonNull Placelist placelist,
                                       @NonNull Universe currentUniverse,
                                       @NonNull Universe searchResultUniverse,
                                       @NonNull Channel channel,
                                       @Nullable String searchQuery) {
        if (mOnEventListener != null) {
            mOnEventListener.onContentSelect(placelist, currentUniverse, searchResultUniverse, channel, searchQuery);
        }
    }

    public void triggerOnDirectionStart(@NonNull Venue venue, Universe universe, DirectionPoint from, DirectionPoint to, String mode, boolean isNavigation) {
        if (mOnEventListener != null) {
            mOnEventListener.onDirectionStart(venue, universe, from, to, mode, isNavigation);
        }
    }

    public void triggerOnUiComponentClick(View view, String name) {
        if (mOnEventListener != null) {
            mOnEventListener.onUiComponentClick(view, name);
        }
    }



}
