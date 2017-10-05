package com.habboproject.server.api.game.rooms.settings;

public enum RoomTradeState {
    DISABLED(0),
    ENABLED(2),
    OWNER_ONLY(1);

    private int state;

    RoomTradeState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public static RoomTradeState valueOf(int state) {
        if (state == 0) return DISABLED;
        else if (state == 2) return ENABLED;
        else return OWNER_ONLY;
    }
}
