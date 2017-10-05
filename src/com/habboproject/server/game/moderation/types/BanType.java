package com.habboproject.server.game.moderation.types;

public enum BanType {
    IP,
    USER,
    MACHINE;

    public static BanType getType(String type) {
        return type.equals("ip") ? IP : type.equals("user") ? USER : MACHINE;
    }
}
