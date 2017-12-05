package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserSavedMottoEvent extends UserEvent
{
    public final String oldMotto;
    public String newMotto;

    /**
     * @param habbo The Habbo this event applies to.
     * @param oldMotto The oldmotto.
     * @param newMotto The new motto.
     */
    public UserSavedMottoEvent(Habbo habbo, String oldMotto, String newMotto)
    {
        super(habbo);
        this.oldMotto = oldMotto;
        this.newMotto = newMotto;
    }
}
