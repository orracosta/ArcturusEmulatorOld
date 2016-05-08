package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 27-3-2015 15:48.
 */
public class RoomUnitSetGoalEvent extends RoomUnitEvent
{
    /**
     * Target goal that the Habbo has set.
     */
    public final Tile goal;

    public RoomUnitSetGoalEvent(RoomUnit roomUnit, Tile goal)
    {
        super(roomUnit);

        this.goal = goal;
    }

    /**
     * Sets a new walk goal location for the Habbo.
     * This will trigger an new event instance of this class.
     * @param t an location.
     */
    public void setGoal(Tile t)
    {
        super.roomUnit.setGoalLocation(t);
    }
}
