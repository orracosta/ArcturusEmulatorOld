package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

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
        this.response.init(Outgoing.AchievementListComposer);

        try
        {
            synchronized (Emulator.getGameEnvironment().getAchievementManager().getAchievements())
            {
                this.response.appendInt(Emulator.getGameEnvironment().getAchievementManager().getAchievements().size());

                for (Achievement achievement : Emulator.getGameEnvironment().getAchievementManager().getAchievements().values())
                {
                    int achievementProgress;
                    AchievementLevel currentLevel = null;
                    AchievementLevel nextLevel = null;

                    achievementProgress = this.habbo.getHabboStats().getAchievementProgress(achievement);
                    currentLevel = achievement.getLevelForProgress(achievementProgress);
                    nextLevel = achievement.getNextLevel(currentLevel != null ? currentLevel.level : 0);

                    if(currentLevel != null && currentLevel.level == achievement.levels.size())
                        nextLevel = null;

                    int targetLevel = 1;

                    if(nextLevel != null)
                        targetLevel = nextLevel.level;

                    if(currentLevel != null && currentLevel.level == achievement.levels.size())
                        targetLevel = currentLevel.level;


                    this.response.appendInt(achievement.id); //ID
                    this.response.appendInt(targetLevel); //Target level
                    this.response.appendString("ACH_" + achievement.name + targetLevel); //Target badge code
                    this.response.appendInt(currentLevel != null ? currentLevel.progress : 0); //Last level progress needed
                    this.response.appendInt(nextLevel != null ? nextLevel.progress : 0); //Progress needed
                    this.response.appendInt(nextLevel != null ? nextLevel.rewardAmount : 0); //Reward amount
                    this.response.appendInt(nextLevel != null ? nextLevel.rewardType : 0); //Reward currency ID
                    this.response.appendInt(achievementProgress == -1 ? -1 : achievementProgress); //Current progress
                    this.response.appendBoolean(AchievementManager.hasAchieved(habbo, achievement)); //Achieved? (Current Progress == MaxLevel.Progress)
                    this.response.appendString(achievement.category.toString().toLowerCase()); //Category
                    this.response.appendString(""); //Empty, completly unused in client code
                    this.response.appendInt(achievement.levels.size()); //Count of total levels in this achievement
                    this.response.appendInt(0); //1 = Progressbar visible if the achievement is completed
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