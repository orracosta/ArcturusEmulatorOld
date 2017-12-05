package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnitEffect;
import com.eu.habbo.habbohotel.users.Habbo;

public class SlimeCommand extends Command
{
    public SlimeCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length < 2)
            return false;

        Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(strings[1]);

        if (habbo != gameClient.getHabbo())
        {
            gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.cmd_slime.throws").replace("%username%", habbo.getHabboInfo().getUsername()));

            Emulator.getThreading().run(new Runnable()
            {
                @Override
                public void run()
                {
                    if (Emulator.getRandom().nextInt(100) < 20)
                    {
                        gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.cmd_slime.missed").replace("%username%", habbo.getHabboInfo().getUsername()));
                        return;
                    }

                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(habbo, RoomUnitEffect.SLIMED.getId());
                    habbo.shout(Emulator.getTexts().getValue("essentials.cmd_slime.slimed").replace("%username%", gameClient.getHabbo().getHabboInfo().getUsername()));
                }
            }, (int)gameClient.getHabbo().getRoomUnit().getCurrentLocation().distance(habbo.getRoomUnit().getCurrentLocation()) * 100);
        }

        return true;
    }
}