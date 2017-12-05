package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.rooms.Room;

public class RoomUncachedEvent extends RoomEvent
{
    /**
     * Triggered when the room will be uncached.
     *
     * @param room The room this event applies to.
     */
    public RoomUncachedEvent(Room room)
    {
        super(room);
    }
}