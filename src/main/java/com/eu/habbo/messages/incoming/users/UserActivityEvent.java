package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.messages.incoming.MessageHandler;

public class UserActivityEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String type = this.packet.readString();
        String value = this.packet.readString();

        switch (type)
        {
            case "Quiz":
                if (value.equalsIgnoreCase("7"))
                {
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("SafetyQuizGraduate"));
                }
        }
    }
}