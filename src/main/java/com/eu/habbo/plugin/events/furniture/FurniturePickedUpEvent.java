package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurniturePickedUpEvent extends FurnitureUserEvent
{
    /**
     * This even is trigged whenever a furniture is being picked up.
     * @param furniture The furniture that is being picked up.
     * @param habbo The Habbo that picked it up.
     */
    public FurniturePickedUpEvent(HabboItem furniture, Habbo habbo)
    {
        super(furniture, habbo);
    }
}
