package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.Event;

/**
 * Created on 12-6-2015 17:48.
 */
public abstract class FurnitureEvent extends Event
{
    /**
     * The furniture this event applies to.
     */
    public final HabboItem furniture;

    /**
     * This event is triggered whenever something happens to a furniture in a room.
     * @param furniture The furniture this event applies to.
     */
    public FurnitureEvent(HabboItem furniture)
    {
        this.furniture = furniture;
    }
}
