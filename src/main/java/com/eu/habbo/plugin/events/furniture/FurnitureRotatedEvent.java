package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureRotatedEvent extends FurnitureUserEvent
{
    /**
     * The previous rotation of this furniture.
     */
    public final int oldRotation;

    /**
     * This event is triggered whenever a furniture is being rotated.
     * @param furniture The furniture that is being rotated.
     * @param habbo The Habbo who rotated the furniture.
     * @param oldRotation The previous rotation of this furniture.
     */
    public FurnitureRotatedEvent(HabboItem furniture, Habbo habbo, int oldRotation)
    {
        super(furniture, habbo);

        this.oldRotation = oldRotation;
    }
}
