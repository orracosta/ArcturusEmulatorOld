package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserRightsGivenEvent extends UserEvent
{
    public final Habbo receiver;
    /**
     * @param habbo The Habbo this event applies to.
     * @param receiver The Habbo that received rights.
     */
    public UserRightsGivenEvent(Habbo habbo, Habbo receiver)
    {
        super(habbo);

        this.receiver = receiver;
    }
}
