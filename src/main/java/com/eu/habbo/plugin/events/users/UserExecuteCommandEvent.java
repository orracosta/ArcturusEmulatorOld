package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserExecuteCommandEvent extends UserEvent
{
    /**
     * The command that has been executed.
     */
    public final Command command;

    /**
     * The parameters which has been used to execute the command.
     * This is the whole chat string the Habbo typed split on spaces.
     */
    public final String[] params;

    /**
     * This event is triggered whenever a Habbo executes an command.
     * @param habbo The Habbo this event applies to.
     * @param command The command the Habbo executed.
     * @param params The parameters which has been used to execute the command.
     */
    public UserExecuteCommandEvent(Habbo habbo, Command command, String[] params)
    {
        super(habbo);

        this.command = command;
        this.params = params;
    }
}
