package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 16-10-2015 21:57.
 */
public class HappyHourCommand extends Command
{
    public HappyHourCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_happyhour").split(";");
        super.permission = "cmd_happyhour";
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GenericAlertComposer("Happy Hour!"));

        for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
        {
            AchievementManager.progressAchievement(set.getValue(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("HappyHour"));
        }

        return true;
    }
}
