package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class UpdateGuildPartsCommand extends Command
{
    public UpdateGuildPartsCommand()
    {
        super.permission = "cmd_update_guildparts";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_update_guildparts").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Emulator.getGameEnvironment().getGuildManager().loadGuildParts();

        return true;
    }
}
