package com.habboproject.server.game.navigator.types.featured;

public enum ImageType {
    INTERNAL,
    EXTERNAL;

    public static ImageType get(String t) {
        if (t.equals("internal")) {
            return INTERNAL;
        }
        return EXTERNAL;
    }
}
