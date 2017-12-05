package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;

public class SupportUserKickEvent extends SupportEvent
{
    /**
     * The target to kick.
     */
    public Habbo target;

    /**
     * The message to send upon kick.
     */
    public String message;

    /**
     * This event is triggered when a Habbo gets kicked via the mod tools.
     * @param moderator The moderator for this event.
     * @param target The target to kick.
     * @param message The message to send upon kick.
     */
    public SupportUserKickEvent(Habbo moderator, Habbo target, String message)
    {
        super(moderator);

        this.target = target;
        this.message = message;
    }
}