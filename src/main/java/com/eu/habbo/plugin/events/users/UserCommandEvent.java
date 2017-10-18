package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

public class UserCommandEvent extends UserEvent
{
    /**
     * Arguments the user typed for this command.
     */
    public final String[] args;

    /**
     * Wether the command has succesfully been executed.
     */
    public final boolean succes;

    /**
     * This event is triggered whenever a user executes a command.
     * @param habbo The Habbo this event applies to.
     * @param args Arguments the user typed for this command.
     * @param succes Wether the command has succesfully been executed.
     */
    public UserCommandEvent(Habbo habbo, String[] args, boolean succes)
    {
        super(habbo);
        this.args = args;
        this.succes = succes;
    }
}
