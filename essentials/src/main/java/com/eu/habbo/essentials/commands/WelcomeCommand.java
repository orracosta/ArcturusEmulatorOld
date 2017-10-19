package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

public class WelcomeCommand extends Command
{
    public WelcomeCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length == 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(strings[1]);
            if (habbo != null)
            {
                for (String s : Emulator.getTexts().getValue("essentials.cmd_welcome.text").split(";"))
                {
                    gameClient.getHabbo().shout(s
                            .replace("%username%", habbo.getHabboInfo().getUsername())
                            .replace("%greeter_username%", gameClient.getHabbo().getHabboInfo().getUsername())
                            //.replace("%greeter_rank%", Emulator.getGameEnvironment().getPermissionsManager().getRankName(gameClient.getHabbo().getHabboInfo().getRank()))
                            .replace("%hotelname%", Emulator.getConfig().getValue("hotel.name"))
                            .replace("%onlinecount%", Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "")
                            .replace("%hotelplayername%", Emulator.getConfig().getValue("hotel.player.name"))
                    );
                }
            }

            return true;
        }

        return false;
    }
}