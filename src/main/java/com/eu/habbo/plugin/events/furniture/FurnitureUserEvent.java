package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public abstract class FurnitureUserEvent extends FurnitureEvent
{
    /**
     * The Habbo this event applies to.
     */
    public final Habbo habbo;

    /**
     * This event is triggered whenever something happens to a furniture in a room.
     *
     * @param furniture The furniture this event applies to.
     * @param habbo The Habbo this event applies to.
     */
    public FurnitureUserEvent(HabboItem furniture, Habbo habbo)
    {
        super(furniture);
        this.habbo = habbo;
    }
}
