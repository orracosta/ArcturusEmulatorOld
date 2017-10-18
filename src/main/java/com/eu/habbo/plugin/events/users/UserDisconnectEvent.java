package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserDisconnectEvent extends UserEvent
{
    /**
     * @param habbo The Habbo this event applies to.
     */
    public UserDisconnectEvent(Habbo habbo)
    {
        super(habbo);
    }
}
