package com.habboproject.server.game.navigator.types.featured;

public enum BannerType {
    BIG,
    SMALL;

    public static BannerType get(String t) {
        if (t.equals("big")) {
            return BIG;
        }
        return SMALL;
    }
}
