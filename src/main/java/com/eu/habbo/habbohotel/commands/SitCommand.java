package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;

/**
 * Created on 4-4-2015 12:47.
 */
public class SitCommand extends Command
{
    public SitCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_sit").split(";");
        super.permission = null;
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        gameClient.getHabbo().getHabboInfo().getCurrentRoom().makeSit(gameClient.getHabbo());
        return true;
    }
}
