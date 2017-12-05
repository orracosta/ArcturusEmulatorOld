package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class UserRolledEvent extends UserEvent
{
    /**
     * The roller the RoomUnit has been rolled by.
     */
    public final HabboItem roller;

    /**
     * The new location of the RoomUnit.
     */
    public final RoomTile location;

    /**
     * Called upon any roomunit being rolled on a roller.
     * @param habbo The Habbo this event applies to.
     * @param roller The roller the Habbo has been rolled by.
     * @param location The new location of the Habbo.
     */
    public UserRolledEvent(Habbo habbo, HabboItem roller, RoomTile location)
    {
        super(habbo);

        this.roller = roller;
        this.location = location;
    }
}
