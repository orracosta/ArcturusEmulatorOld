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

    public static RoomUserRotation fromValue(int rotation)
    {
        rotation %= 8;
        for (RoomUserRotation rot : values())
        {
            if (rot.getValue() == rotation)
            {
                return rot;
            }
        }

        return NORTH;
    }

    public static RoomUserRotation counterClockwise(RoomUserRotation rotation)
    {
        return fromValue(rotation.getValue() + 7);
    }

    public static RoomUserRotation clockwise(RoomUserRotation rotation)
    {
        return fromValue(rotation.getValue() + 9);
    }
}
