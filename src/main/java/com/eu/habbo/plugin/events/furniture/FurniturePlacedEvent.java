package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 12-6-2015 17:50.
 */
public class FurniturePlacedEvent extends FurnitureUserEvent
{
    /**
     * The location the furniture was placed.
     * Is NULL when wallitem.
     */
    public final Tile location;

    /**
     * This event is triggered whenever a furniture is being placed down into an room.
     * @param furniture The furniture that is placed down.
     * @param habbo The Habbo who put the furniture to the room.
     * @param location The location the furniture was placed.
     */
    public FurniturePlacedEvent(HabboItem furniture, Habbo habbo, Tile location)
    {
        super(furniture, habbo);

        this.location = location;
    }
}
