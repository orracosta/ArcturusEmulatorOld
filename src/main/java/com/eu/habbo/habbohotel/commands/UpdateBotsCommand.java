package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;

/**
 * Created on 28-1-2016 21:14.
 */
public class UpdateBotsCommand extends Command
{
    public UpdateBotsCommand()
    {
        super.permission = "cmd_update_bots";
        super.keys       = Emulator.getTexts().getValue("commands.keys.cmd_update_bots").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        return Emulator.getGameEnvironment().getBotManager().reload();
    }
}
