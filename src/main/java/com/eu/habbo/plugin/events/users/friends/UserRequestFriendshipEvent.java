package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserRequestFriendshipEvent extends UserEvent
{
    /**
     * The Habbo name invited.
     */
    public final String name;

    /**
     * The Habbo invited.
     */
    public final Habbo friend;

    /**
     * Triggered when a Habbo requests friendship with another user.
     * @param habbo The Habbo this event applies to.
     * @param name The Habbo name invited.
     * @param friend The Habbo invited. NULL when user is offline.
     */
    public UserRequestFriendshipEvent(Habbo habbo, String name, Habbo friend)
    {
        super(habbo);

        this.name = name;
        this.friend = friend;
    }
}
