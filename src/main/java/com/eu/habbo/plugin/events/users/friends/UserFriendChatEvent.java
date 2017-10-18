package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserFriendChatEvent extends UserFriendEvent
{
    /**
     * The message send.
     */
    public String message;

    /**
     * Triggered whenever a Habbo sends an IM message.
     * @param habbo  The Habbo this event applies to.
     * @param friend The friend this event applies to.
     * @param message The message being send.
     */
    public UserFriendChatEvent(Habbo habbo, MessengerBuddy friend, String message)
    {
        super(habbo, friend);

        this.message = message;
    }
}
