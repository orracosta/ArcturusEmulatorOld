package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

public class ShutdownCommand extends Command
{
    public ShutdownCommand()
    {
        super("cmd_shutdown", Emulator.getTexts().getValue("commands.keys.cmd_shutdown").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        String reason = "-";

        if(params.length > 1)
        {
            reason = "";
            for(int i = 1; i < params.length; i++)
            {
                reason += params[i] + " ";
            }
        }

        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GenericAlertComposer("<b>" + Emulator.getTexts().getValue("generic.warning") + "</b> \r\n" +
                Emulator.getTexts().getValue("generic.shutdown") + "\r\n" +
                Emulator.getTexts().getValue("generic.reason.specified") + ": <b>" + reason + "</b>\r" +
                "\r" +
                "- " + gameClient.getHabbo().getHabboInfo().getUsername()
        ));

        Emulator.getRuntime().exit(0);
        return true;
    }
}
