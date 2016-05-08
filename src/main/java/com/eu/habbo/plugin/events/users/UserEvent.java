package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.Event;

/**
 * Created on 28-2-2015 19:53.
 */
public abstract class UserEvent extends Event
{
    /**
     * The Habbo this event applies to.
     */
    public final Habbo habbo;

    /**
     * @param habbo The Habbo this event applies to.
     */
    public UserEvent(Habbo habbo)
    {
        this.habbo = habbo;
    }
}
