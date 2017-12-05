package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

public class RoomUnitSetGoalEvent extends RoomUnitEvent
{
    /**
     * Target goal that the Habbo has set.
     */
    public final RoomTile goal;

    public RoomUnitSetGoalEvent(Room room, RoomUnit roomUnit, RoomTile goal)
    {
        super(room, roomUnit);

        this.goal = goal;
    }

    /**
     * Sets a new walk goal location for the Habbo.
     * This will trigger an new event instance of this class.
     * @param t an location.
     */
    public void setGoal(RoomTile t)
    {
        super.roomUnit.setGoalLocation(t);
    }
}
