package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserExitRoomEvent extends UserEvent
{
    public enum UserExitRoomReason
    {
        DOOR(false),
        KICKED_HABBO(false),
        KICKED_IDLE(true);

        public final boolean cancellable;

        UserExitRoomReason(boolean cancellable)
        {
            this.cancellable = cancellable;
        }
    }

    public final UserExitRoomReason reason;

    /**
     * @param habbo The Habbo this event applies to.
     * @param reason The reason the Habbo has left the room.
     */
    public UserExitRoomEvent(Habbo habbo, UserExitRoomReason reason)
    {
        super(habbo);
        this.reason = reason;
    }
}