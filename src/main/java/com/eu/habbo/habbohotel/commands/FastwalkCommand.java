package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 21-11-2015 20:53.
 */
public class FastwalkCommand extends Command
{
    public FastwalkCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_fastwalk").split(";");
        super.permission = "cmd_fastwalk";
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            Habbo habbo = gameClient.getHabbo();

            if(params.length >= 2)
            {
                String username = params[1];

                habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(username);

                if(habbo == null)
                    return false;
            }
            habbo.getRoomUnit().setFastWalk(!habbo.getRoomUnit().isFastWalk());

            return true;
        }

        return false;
    }
}
