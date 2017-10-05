package com.habboproject.server.api.game.rooms.settings;

public enum RoomMuteState {
    NONE(0),
    RIGHTS(1);

    private int state;

    RoomMuteState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public static RoomMuteState valueOf(int state) {
        if (state == 0) return NONE;
        else return RIGHTS;
    }
}
