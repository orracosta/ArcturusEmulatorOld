package com.eu.habbo.plugin.events.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.plugin.Event;

public abstract class RoomEvent extends Event
{
    /**
     * The Room this event applies to.
     */
    public final Room room;

    /**
     * This is an event that applies to a certain room.
     * @param room The room this event applies to.
     */
    public RoomEvent(Room room)
    {
        this.room = room;
    }
}