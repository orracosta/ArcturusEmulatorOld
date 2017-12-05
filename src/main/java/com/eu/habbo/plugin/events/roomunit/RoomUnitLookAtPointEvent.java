package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

public class RoomUnitLookAtPointEvent extends RoomUnitEvent
{
    /**
     * The tile to look at.
     */
    public final RoomTile location;

    /**
     * @param room The Room this event applies to.
     * @param roomUnit The RoomUnit this event applies to.
     * @param location The tile to look at.
     */
    public RoomUnitLookAtPointEvent(Room room, RoomUnit roomUnit, RoomTile location)
    {
        super(room, roomUnit);

        this.location = location;
    }
}
