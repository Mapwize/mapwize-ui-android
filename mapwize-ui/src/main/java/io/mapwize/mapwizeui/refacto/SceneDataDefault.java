package io.mapwize.mapwizeui.refacto;

public class SceneDataDefault {

    String language;

    private SceneDataDefault(Builder builder) {
        this.language = builder.language;
    }

    public static class Builder {

        String language = "en";

        Builder language(String language) {
            this.language = language;
            return this;
        }

        SceneDataDefault build() {
            return new SceneDataDefault(this);
        }
    }

}
