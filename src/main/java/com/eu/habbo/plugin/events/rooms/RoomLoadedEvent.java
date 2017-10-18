package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.rooms.Room;

public class RoomLoadedEvent extends RoomEvent
{
    /**
     * Triggered whenever a room is fully loaded.
     *
     * @param room The room that has been loaded.
     */
    public RoomLoadedEvent(Room room)
    {
        super(room);
    }
}