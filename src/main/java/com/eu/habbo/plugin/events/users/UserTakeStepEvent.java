package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserTakeStepEvent extends UserEvent
{
    /**
     * The old location of the Habbo.
     */
    public final RoomTile fromLocation;

    /**
     * The new location of the Habbo.
     */
    public final RoomTile toLocation;

    /**
     * This event is triggered each time an Habbo walks to the next square.
     * @param habbo The Habbo this event applies to.
     * @param fromLocation The old location of the Habbo.
     * @param toLocation The new location of the Habbo.
     */
    public UserTakeStepEvent(Habbo habbo, RoomTile fromLocation, RoomTile toLocation)
    {
        super(habbo);

        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }
}
