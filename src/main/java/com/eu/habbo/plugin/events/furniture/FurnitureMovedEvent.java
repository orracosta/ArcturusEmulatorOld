package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 12-6-2015 17:49.
 */
public class FurnitureMovedEvent extends FurnitureUserEvent
{
    /**
     * The old location of the furniture being moved.
     */
    public final Tile oldPosition;

    /**
     * The new location of the furniture being moved.
     */
    public final Tile newPosition;

    /**
     * This event is triggered whenever an furniture is being moved.
     * @param furniture The furniture that is being moved.
     * @param oldPosition The old location of the furniture being moved.
     * @param newPosition The new location of the furniture being moved.
     * @param habbo The Habbo who moved the furniture.
     */
    public FurnitureMovedEvent(HabboItem furniture, Habbo habbo, Tile oldPosition, Tile newPosition)
    {
        super(furniture, habbo);

        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
}
