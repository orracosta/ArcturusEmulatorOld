package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserRegisteredEvent extends UserEvent
{
    /**
     * This event is called whenever a user logs in for the first time to the hotel.
     * @param habbo The Habbo this event applies to.
     */
    public UserRegisteredEvent(Habbo habbo)
    {
        super(habbo);
    }
}
