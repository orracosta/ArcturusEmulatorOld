package com.eu.habbo.plugin.events.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class NavigatorRoomCreatedEvent extends UserEvent
{
    /**
     * The room created.
     */
    public final Room room;

    /**
     * Triggered whenever a user creates a room.
     * This event cannot be cancelled.
     * @param habbo The Habbo this event applies to.
     * @param room The room created.
     */
    public NavigatorRoomCreatedEvent(Habbo habbo, Room room)
    {
        super(habbo);

        this.room = room;
    }
}
