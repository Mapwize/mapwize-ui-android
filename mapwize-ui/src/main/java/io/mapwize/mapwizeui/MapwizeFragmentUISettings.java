package io.mapwize.mapwizeui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Provides some options to configure the MapwizeUIFragment
 * UISettings cannot be changed after initialization
 */
public class MapwizeFragmentUISettings implements Parcelable {

    private boolean menuButtonHidden;
    private boolean followUserButtonHidden;
    private boolean floorControllerHidden;
    private boolean compassHidden;

    private MapwizeFragmentUISettings(boolean menuButtonHidden, boolean followUserButtonHidden, boolean floorControllerHidden, boolean compassHidden) {
        this.menuButtonHidden = menuButtonHidden;
        this.followUserButtonHidden = followUserButtonHidden;
        this.floorControllerHidden = floorControllerHidden;
        this.compassHidden = compassHidden;
    }

    public boolean isMenuButtonHidden() {
        return menuButtonHidden;
    }

    public boolean isFollowUserButtonHidden() {
        return followUserButtonHidden;
    }

    public boolean isFloorControllerHidden() {
        return floorControllerHidden;
    }

    public boolean isCompassHidden() {
        return compassHidden;
    }

    private MapwizeFragmentUISettings(Parcel in) {
        menuButtonHidden = in.readByte() != 0;
        followUserButtonHidden = in.readByte() != 0;
        floorControllerHidden = in.readByte() != 0;
        compassHidden = in.readByte() != 0;
    }

    public static final Creator<MapwizeFragmentUISettings> CREATOR = new Creator<MapwizeFragmentUISettings>() {
        @Override
        public MapwizeFragmentUISettings createFromParcel(Parcel in) {
            return new MapwizeFragmentUISettings(in);
        }

        @Override
        public MapwizeFragmentUISettings[] newArray(int size) {
            return new MapwizeFragmentUISettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (menuButtonHidden ? 1 : 0));
        dest.writeByte((byte) (followUserButtonHidden ? 1 : 0));
        dest.writeByte((byte) (floorControllerHidden ? 1 : 0));
        dest.writeByte((byte) (compassHidden ? 1 : 0));
    }

    @Override
    public String toString() {
        return "MapwizeFragmentUISettings{" +
                "menuButtonHidden=" + menuButtonHidden +
                ", followUserButtonHidden=" + followUserButtonHidden +
                ", floorControllerHidden=" + floorControllerHidden +
                ", compassHidden=" + compassHidden +
                '}';
    }

    public static class Builder {

        private boolean menuButtonHidden;
        private boolean followUserButtonHidden;
        private boolean floorControllerHidden;
        private boolean compassHidden;

        public Builder() {
            this.menuButtonHidden = false;
            this.followUserButtonHidden = false;
            this.floorControllerHidden = false;
            this.compassHidden = false;
        }

        /**
         * Show/Hide the menu button in the search bar
         * @param isHidden true if you want to hide the menu button
         * @return the builder
         */
        public Builder menuButtonHidden(boolean isHidden) {
            this.menuButtonHidden = isHidden;
            return this;
        }

        /**
         * Show/Hide the compass
         * @param isHidden true if you want to hide the compass
         * @return the builder
         */
        public Builder compassHidden(boolean isHidden) {
            this.compassHidden = isHidden;
            return this;
        }

        /**
         * Show/Hide the follow user button
         * @param isHidden true if you want to hide the follow user button
         * @return the builder
         */
        public Builder followUserButtonHidden(boolean isHidden) {
            this.followUserButtonHidden = isHidden;
            return this;
        }

        /**
         * Show/Hide the floor controller
         * @param isHidden true if you want to hide the floor controller
         * @return the builder
         */
        public Builder floorControllerHidden(boolean isHidden) {
            this.floorControllerHidden = isHidden;
            return this;
        }

        /**
         * Build the fragment UISettings
         * @return the MapwizeFragmentUISettings
         */
        public MapwizeFragmentUISettings build() {
            return new MapwizeFragmentUISettings(this.menuButtonHidden, this.followUserButtonHidden, this.floorControllerHidden, this.compassHidden);
        }

    }
}
