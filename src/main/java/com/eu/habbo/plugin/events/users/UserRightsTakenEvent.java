package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 25-12-2015 17:02.
 */
public class UserRightsTakenEvent extends UserEvent
{
    public final int victimId;
    public final Habbo victim;

    /**
     * @param habbo The Habbo this event applies to.
     * @param victimId The userid.
     * @param victim The Habbo whose rights got taken. Not null when the victim is in the room.
     */
    public UserRightsTakenEvent(Habbo habbo, int victimId, Habbo victim)
    {
        super(habbo);

        this.victimId = victimId;
        this.victim = victim;
    }
}
