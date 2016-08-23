package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.iterator.TIntObjectIterator;

import java.util.Map;

public class AchievementUpdater implements Runnable
{
    @Override
    public void run()
    {
        Emulator.getThreading().run(this, 5 * 60);

        Achievement onlineTime = Emulator.getGameEnvironment().getAchievementManager().getAchievement("AllTimeHotelPresence");

        for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
        {
            Habbo habbo = set.getValue();

            int timeOnline = habbo.getHabboStats().getAchievementCache().get(onlineTime);
            if(timeOnline == 0)
            {
                habbo.getHabboStats().getAchievementCache().put(onlineTime, Emulator.getIntUnixTimestamp());
            }

            AchievementManager.progressAchievement(habbo, onlineTime, (int)Math.floor((Emulator.getIntUnixTimestamp() - timeOnline) / 60));
            habbo.getHabboStats().getAchievementCache().put(onlineTime, timeOnline % 60);
        }
    }
}
