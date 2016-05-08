package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

/**
 * Created on 28-8-2015 10:58.
 */
public class FurnitureBoughtEvent extends FurnitureUserEvent
{
    /**
     * This event is triggered whenever someone buys a furniture.
     * Cancelling this event will not prevent the furniture from being bought.
     * @param furniture The furniture this event applies to.
     * @param habbo     The Habbo this event applies to.
     */
    public FurnitureBoughtEvent(HabboItem furniture, Habbo habbo)
    {
        super(furniture, habbo);
    }
}
