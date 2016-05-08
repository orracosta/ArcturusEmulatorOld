package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 13-11-2015 21:53.
 */
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
