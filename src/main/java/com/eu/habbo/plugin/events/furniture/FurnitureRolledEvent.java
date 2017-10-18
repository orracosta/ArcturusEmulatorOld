package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureRolledEvent extends FurnitureEvent
{
    /**
     * The roller that moved the furniture.
     */
    public final HabboItem roller;

    /**
     * The new location of the furniture.
     */
    public final RoomTile newLocation;

    /**
     * This event is triggered when an furniture is being rolled by a roller.
     * @param furniture The furniture that is being rolled.
     * @param roller The roller who moved the furniture.
     * @param newLocation The new location of the furniture.
     */
    public FurnitureRolledEvent(HabboItem furniture, HabboItem roller, RoomTile newLocation)
    {
        super(furniture);

        this.roller = roller;
        this.newLocation = newLocation;
    }
}
