package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserAcceptFriendRequestEvent extends UserFriendEvent
{
    /**
     * Triggered when a Habbo accepts a new friend.
     * @param habbo  The Habbo this event applies to.
     * @param friend The friend this event applies to.
     */
    public UserAcceptFriendRequestEvent(Habbo habbo, MessengerBuddy friend)
    {
        super(habbo, friend);
    }
}
