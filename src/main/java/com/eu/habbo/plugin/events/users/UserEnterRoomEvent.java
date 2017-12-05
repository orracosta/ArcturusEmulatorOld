package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserEnterRoomEvent extends UserEvent
{
    /**
     * The room being entered.
     */
    public final Room room;

    /**
     * Triggered when a Habbo enters a room.
     * Cancel the event to prevent the Habbo from entering.
     *
     * @param habbo The Habbo this event applies to.
     * @param room The room being entered.
     */
    public UserEnterRoomEvent(Habbo habbo, Room room)
    {
        super(habbo);

        this.room = room;
    }
}
