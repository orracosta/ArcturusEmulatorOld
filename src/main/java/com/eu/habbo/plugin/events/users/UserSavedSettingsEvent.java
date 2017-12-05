package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserSavedSettingsEvent extends UserEvent
{
    /**
     * Fired when a user changes it settings regarding:
     * - Volumes
     * - Camera following
     * - Ignoring room invites
     * - Chat mode.
     * @param habbo The Habbo this event applies to.
     */
    public UserSavedSettingsEvent(Habbo habbo)
    {
        super(habbo);
    }
}
