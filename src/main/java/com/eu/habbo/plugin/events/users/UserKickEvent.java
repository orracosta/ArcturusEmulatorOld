package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserKickEvent extends UserEvent
{
    /**
     * The Habbo being kicked.
     */
    public final Habbo target;

    /**
     * @param habbo The Habbo this event applies to.
     */
    public UserKickEvent(Habbo habbo, Habbo target)
    {
        super(habbo);

        this.target = target;
    }
}
