package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.rooms.Room;

public class RoomUnloadedEvent extends RoomEvent
{
    /**
     * Triggered whenever a room has been unloaded.
     *
     * @param room The room that has been unloaded.
     */
    public RoomUnloadedEvent(Room room)
    {
        super(room);
    }
}