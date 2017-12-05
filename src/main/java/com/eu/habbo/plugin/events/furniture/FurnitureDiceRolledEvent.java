package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureDiceRolledEvent extends FurnitureUserEvent
{
    /**
     * The result that will be displayed.
     */
    public int result;

    /**
     * This event is triggered when a dice is rolled.
     * @param furniture The furniture this event applies to.
     * @param habbo The Habbo who rolled the dice.
     * @param result The result that will be displayed.
     */
    public FurnitureDiceRolledEvent(HabboItem furniture, Habbo habbo, int result)
    {
        super(furniture, habbo);

        this.result = result;
    }
}
