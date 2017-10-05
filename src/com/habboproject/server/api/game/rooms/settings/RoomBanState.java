package com.habboproject.server.api.game.rooms.settings;

public enum RoomBanState {
    NONE(0),
    RIGHTS(1);

    private int state;

    RoomBanState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public static RoomBanState valueOf(int state) {
        if (state == 0) return NONE;
        else return RIGHTS;
    }
}
