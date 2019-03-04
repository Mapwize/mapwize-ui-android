package io.mapwize.mapwizecomponents.ui;

public class Floor {

    private Double rawValue;
    private String displayName;
    private boolean selected;

    public Floor(Double rawValue, String displayName) {
        this.rawValue = rawValue;
        this.displayName = displayName;
        this.selected = false;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Double getRawValue() {
        return rawValue;
    }

    public String getDisplayName() {
        return displayName;
    }
}
