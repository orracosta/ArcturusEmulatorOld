package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

public class BotsCommand extends Command
{
    public BotsCommand()
    {
        super("cmd_bots", Emulator.getTexts().getValue("commands.keys.cmd_bots").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null || !gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo()))
            return false;

        String data = Emulator.getTexts().getValue("total") + ": " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentBots().values().length;

        for(Object bot : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentBots().values())
        {
            if(bot instanceof Bot)
            {
                data += "\r";
                data += "<b>" + Emulator.getTexts().getValue("generic.bot.name") + "</b>: " + ((Bot) bot).getName() + " <b>" + Emulator.getTexts().getValue("generic.bot.id") + "</b>: " + ((Bot) bot).getId();
            }
        }

        gameClient.sendResponse(new GenericAlertComposer(data));

        return true;
    }
}
