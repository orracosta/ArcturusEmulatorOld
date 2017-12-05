package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserCreditsEvent extends UserEvent
{
    /**
     * The amount of credits that will be added or removed.
     */
    public int credits;

    /**
     * This Event is triggered when credits are added or removed.
     * @param habbo The Habbo this event applies to.
     * @param credits The amount of credits that will be added or removed.
     */
    public UserCreditsEvent(Habbo habbo, int credits)
    {
        super(habbo);

        this.credits = credits;
    }
}