package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomUnitEffect;
import com.eu.habbo.habbohotel.users.Habbo;

public class NukePlayerCommand extends Command
{
    public NukePlayerCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length < 2)
        {
            gameClient.getHabbo().talk(Emulator.getTexts().getValue("habbo.not.found"));
            return false;
        }

        Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(strings[1]);
        if (habbo == null)
        {
            gameClient.getHabbo().talk(Emulator.getTexts().getValue("habbo.not.found"));
            return false;
        }

        if (habbo.getHabboInfo().getId() == gameClient.getHabbo().getHabboInfo().getId())
        {
            gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.nuke.self"));
            return false;
        }

        gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.nuke.action").replace("%username%", habbo.getHabboInfo().getUsername()));

        Emulator.getThreading().run(new Runnable()
        {
            @Override
            public void run()
            {
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(habbo, RoomUnitEffect.NINJADISAPPEAR.getId());
                habbo.shout(Emulator.getTexts().getValue("essentials.nuke.nuked").replace("%username%", habbo.getHabboInfo().getUsername()));
            }
        }, 500);

        return true;
    }
}