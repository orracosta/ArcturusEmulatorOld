package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;

public class UserSavedLookEvent extends UserEvent
{
    public HabboGender gender;
    public String newLook;

    /**
     * Triggered whenever a Habbo saved its look.
     * newLook can be altered to override the look it saves to.
     * @param habbo The Habbo this event applies to.
     * @param gender The gender of this look.
     * @param newLook The new look.
     */
    public UserSavedLookEvent(Habbo habbo, HabboGender gender, String newLook)
    {
        super(habbo);
        this.gender = gender;
        this.newLook = newLook;
    }
}
