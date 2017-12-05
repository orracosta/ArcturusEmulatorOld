package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureMovedEvent extends FurnitureUserEvent
{
    /**
     * The old location of the furniture being moved.
     */
    public final RoomTile oldPosition;

    /**
     * The new location of the furniture being moved.
     */
    public final RoomTile newPosition;

    /**
     * This event is triggered whenever an furniture is being moved.
     * @param furniture The furniture that is being moved.
     * @param oldPosition The old location of the furniture being moved.
     * @param newPosition The new location of the furniture being moved.
     * @param habbo The Habbo who moved the furniture.
     */
    public FurnitureMovedEvent(HabboItem furniture, Habbo habbo, RoomTile oldPosition, RoomTile newPosition)
    {
        super(furniture, habbo);

        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
}
