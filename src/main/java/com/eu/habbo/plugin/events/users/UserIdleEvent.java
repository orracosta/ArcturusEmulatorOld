package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserIdleEvent extends UserEvent
{
    public enum IdleReason
    {
        ACTION,
        DANCE,
        TIMEOUT,
        WALKED,
        TALKED
    }

    public final IdleReason reason;
    public boolean idle;

    /**
     * Triggered whenever the state of idle for a given Habbo changes.
     * @param habbo The Habbo this event applies to.
     * @param reason The cause of this event.
     * @param idle The new idle state.
     */
    public UserIdleEvent(Habbo habbo, IdleReason reason, boolean idle)
    {
        super(habbo);

        this.reason = reason;
        this.idle = idle;
    }
}