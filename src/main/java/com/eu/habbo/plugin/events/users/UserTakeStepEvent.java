package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.AbstractNode;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 10-4-2015 19:41.
 */
public class UserTakeStepEvent extends UserEvent
{
    /**
     * The old location of the Habbo.
     */
    public final Tile fromLocation;

    /**
     * The new location of the Habbo.
     */
    public final AbstractNode toLocation;

    /**
     * This event is triggered each time an Habbo walks to the next square.
     * @param habbo The Habbo this event applies to.
     * @param fromLocation The old location of the Habbo.
     * @param toLocation The new location of the Habbo.
     */
    public UserTakeStepEvent(Habbo habbo, Tile fromLocation, AbstractNode toLocation)
    {
        super(habbo);

        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
    }
}
