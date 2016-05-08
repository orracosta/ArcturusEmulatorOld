package com.eu.habbo.habbohotel.rooms;

/**
 * Created on 13-9-2014 17:14.
 */
public enum RoomUserRotation {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int direction;
    RoomUserRotation(int direction)
    {
        this.direction = direction;
    }

    public int getValue()
    {
        return this.direction;
    }
}
