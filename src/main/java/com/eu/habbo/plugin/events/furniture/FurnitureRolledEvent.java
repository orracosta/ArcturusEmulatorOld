package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.Tile;

public class FurnitureRolledEvent extends FurnitureEvent
{
    /**
     * The roller that moved the furniture.
     */
    public final HabboItem roller;

    /**
     * The new location of the furniture.
     */
    public final Tile newLocation;

    /**
     * This event is triggered when an furniture is being rolled by a roller.
     * @param furniture The furniture that is being rolled.
     * @param roller The roller who moved the furniture.
     * @param newLocation The new location of the furniture.
     */
    public FurnitureRolledEvent(HabboItem furniture, HabboItem roller, Tile newLocation)
    {
        super(furniture);

        this.roller = roller;
        this.newLocation = newLocation;
    }
}
