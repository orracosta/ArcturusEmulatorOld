package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public abstract class Command
{
    /**
     * Permission string the executor must have in order to access the command.
     */
    public final String permission;

    /**
     * All keys the command can be exectuted from.
     * <p>
     *     Example:
     *     :<b>userinfo</b> TheGeneral
     * </p>
     */
    public final String[] keys;

    public Command(String permission, String[] keys)
    {
        this.permission = permission;
        this.keys = keys;
    }

    /**
     * Executes an command for a given gameClient.
     * @param gameClient The GameClient who executes the command.
     * @param params Array of parameters given in the command.
     * @return Returns true if the user should <b>NOT</b> say anything in the room.
     * @throws Exception
     */
    public abstract boolean handle(GameClient gameClient, String[] params) throws Exception;
}