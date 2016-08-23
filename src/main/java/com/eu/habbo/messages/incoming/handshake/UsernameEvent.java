package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.util.Calendar;
import java.util.Date;

public class UsernameEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login")))
        {
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login"));
        }
        else
        {
            Date lastLogin = new Date(this.client.getHabbo().getHabboInfo().getLastOnline());
            Calendar c1 = Calendar.getInstance(); // today
            c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

            Calendar c2 = Calendar.getInstance();
            c2.setTime(lastLogin); // your date

            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
            {
                if (this.client.getHabbo().getHabboStats().getAchievementProgress().get(Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login")) == this.client.getHabbo().getHabboStats().loginStreak)
                {
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Login"));
                    this.client.getHabbo().getHabboStats().loginStreak++;
                }
            } else
            {
                if (((lastLogin.getTime() / 1000) - Emulator.getIntUnixTimestamp()) > 86400)
                {
                    this.client.getHabbo().getHabboStats().loginStreak = 0;
                }
            }
        }

        if (!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration")))
        {
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration"), 0);
        }
        else
        {
            int daysRegistered = ((this.client.getHabbo().getHabboInfo().getAccountCreated() - Emulator.getIntUnixTimestamp()) / 86400);

            int days = this.client.getHabbo().getHabboStats().getAchievementProgress(
                    Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration")
            );

            if (daysRegistered - days > 0)
            {
                AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RegistrationDuration"), daysRegistered - days);
            }
        }

        if(!this.client.getHabbo().getHabboStats().getAchievementProgress().containsKey(Emulator.getGameEnvironment().getAchievementManager().getAchievement("TraderPass")))
        {
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("TraderPass"));
        }
    }
}
