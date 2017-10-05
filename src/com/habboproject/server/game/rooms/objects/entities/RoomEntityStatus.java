package com.habboproject.server.game.rooms.objects.entities;

public enum RoomEntityStatus {
    SIT("sit"),
    MOVE("mv"),
    LAY("lay"),
    SIGN("sign"),
    CONTROLLER("flatctrl"),
    TRADE("trd"),
    VOTE("vote"),
    GESTURE("gst"),
    PLAY("pla"),
    PLAY_DEAD("ded"),
    JUMP("jmp");

    private final String statusCode;

    RoomEntityStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return this.statusCode;
    }
}
