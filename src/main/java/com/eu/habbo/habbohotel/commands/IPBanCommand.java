package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.users.Habbo;

public class IPBanCommand extends Command
{
    public final static int TEN_YEARS = 315569260;
    public IPBanCommand()
    {
        super("cmd_ip_ban", Emulator.getTexts().getValue("commands.keys.cmd_ip_ban").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Habbo habbo = null;
        String reason = "";
        if (params.length >= 2)
        {
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);
        }

        if (params.length > 2)
        {
            for (int i = 2; i < params.length; i++)
            {
                reason += params[i];
                reason += " ";
            }
        }

        if (habbo != null)
        {
            int time = Emulator.getIntUnixTimestamp() + TEN_YEARS;
            ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().createBan(habbo, gameClient.getHabbo(), time, reason, "ip");

            for (Habbo h : Emulator.getGameServer().getGameClientManager().getHabbosWithIP(ban.ip))
            {
                if (h != null)
                {
                    Emulator.getGameEnvironment().getModToolManager().createBan(h, gameClient.getHabbo(), time, reason, "ip");
                }
            }
        }

        return true;
    }
}
