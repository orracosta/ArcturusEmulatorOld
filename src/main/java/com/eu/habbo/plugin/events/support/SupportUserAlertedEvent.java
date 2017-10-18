package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;

public class SupportUserAlertedEvent extends SupportEvent
{
    /**
     * The Habbo this event applies to.
     */
    public Habbo target;

    /**
     * The message send to the habbo.
     */
    public String message;

    /**
     * This event is triggered when a modeerator alerts an user.
     * @param moderator The moderator for this event.
     * @param target The Habbo this event applies to.
     * @param message The message send to the habbo.
     */
    public SupportUserAlertedEvent(Habbo moderator, Habbo target, String message)
    {
        super(moderator);

        this.message = message;
        this.target  = target;
    }
}