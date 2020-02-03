package io.mapwize.mapwizeui.events;

public enum Channel {
    MAP_CLICK(1),
    SEARCH(2),
    MAIN_SEARCHES(3);
    public final int value;
    Channel(int value) {
        this.value = value;
    }
}