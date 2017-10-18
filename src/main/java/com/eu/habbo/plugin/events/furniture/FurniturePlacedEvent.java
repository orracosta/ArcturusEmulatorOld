package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurniturePlacedEvent extends FurnitureUserEvent
{
    /**
     * The location the furniture was placed.
     * Is NULL when wallitem.
     */
    public final RoomTile location;

    /**
     * This event is triggered whenever a furniture is being placed down into an room.
     * @param furniture The furniture that is placed down.
     * @param habbo The Habbo who put the furniture to the room.
     * @param location The location the furniture was placed.
     */
    public FurniturePlacedEvent(HabboItem furniture, Habbo habbo, RoomTile location)
    {
        super(furniture, habbo);

        this.location = location;
    }
}
