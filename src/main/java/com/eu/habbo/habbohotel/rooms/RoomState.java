package com.eu.habbo.habbohotel.rooms;

/**
 * Created on 31-8-2014 12:54.
 */
public enum RoomState {
    OPEN(0), LOCKED(1), PASSWORD(2), INVISIBLE(3);

    private final int state;

    RoomState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return this.state;
    }
}
