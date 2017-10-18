package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.Event;

public abstract class SupportEvent extends Event
{
    /**
     * The moderator for this event.
     */
    public Habbo moderator;

    /**
     * @param moderator The moderator for this event.
     */
    public SupportEvent(Habbo moderator)
    {
        this.moderator = moderator;
    }
}