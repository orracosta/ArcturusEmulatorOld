package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.rooms.Room;

public class RoomUnloadingEvent extends RoomEvent
{
    /**
     * Triggered when a room is about to be unloaded.
     *
     * @param room The room this event applies to.
     */
    public RoomUnloadingEvent(Room room)
    {
        super(room);
    }
}