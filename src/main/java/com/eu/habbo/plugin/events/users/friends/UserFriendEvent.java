package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public abstract class UserFriendEvent extends UserEvent
{
    public final MessengerBuddy friend;

    /**
     * @param habbo The Habbo this event applies to.
     * @param friend The friend this event applies to.
     */
    public UserFriendEvent(Habbo habbo, MessengerBuddy friend)
    {
        super(habbo);

        this.friend = friend;
    }
}
