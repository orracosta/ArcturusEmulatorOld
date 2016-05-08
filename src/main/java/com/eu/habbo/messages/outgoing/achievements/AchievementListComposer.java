package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 30-1-2015 13:55.
 */
public class AchievementListComposer extends MessageComposer
{
    private final Habbo habbo;

    public AchievementListComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AchievementListComposer); //TODO

        try
        {
            synchronized (Emulator.getGameEnvironment().getAchievementManager().achievements)
            {
                this.response.appendInt32(Emulator.getGameEnvironment().getAchievementManager().achievements.size());

                for (Achievement achievement : Emulator.getGameEnvironment().getAchievementManager().achievements.values())
                {
                    int achievementProgress;
                    AchievementLevel level;
                    AchievementLevel oldLevel;

                    achievementProgress = this.habbo.getHabboStats().getAchievementProgress(achievement);
                    level = achievement.getLevelForProgress(achievementProgress);
                    oldLevel = achievement.levels.get((level != null) ? level.level - 1 : -1);
                    this.response.appendInt32(achievement.id); //ID
                    this.response.appendInt32(achievementProgress == -1 ? 1 : (level == null) ? 1 : level.level);
                    this.response.appendString("ACH_" + achievement.name + ((level == null ? 1 : level.level)));
                    this.response.appendInt32(oldLevel != null ? oldLevel.progress : 1);
                    this.response.appendInt32(level != null ? level.progress : 1);
                    this.response.appendInt32(level != null ? level.pixels : 0);
                    this.response.appendInt32(0);
                    this.response.appendInt32(achievementProgress == -1 ? 0 : achievementProgress); //Current Progress
                    this.response.appendBoolean(AchievementManager.hasAchieved(habbo, achievement)); //Achieved? (Current Progress == Level.Progress)
                    this.response.appendString(achievement.category.toString().toLowerCase());
                    this.response.appendString("");
                    this.response.appendInt32(achievement.levels.size()); //Count of total levels in this achievement.
                    this.response.appendInt32(0); //IDK
                }

                this.response.appendString("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.response;
    }
}
