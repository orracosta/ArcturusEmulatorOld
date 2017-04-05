package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AchievementProgressComposer extends MessageComposer
{
    private final Habbo habbo;
    private final Achievement achievement;

    public AchievementProgressComposer(Habbo habbo, Achievement achievement)
    {
        this.habbo = habbo;
        this.achievement = achievement;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AchievementProgressComposer);

        int achievementProgress;
        AchievementLevel level;
        AchievementLevel nextLevel;

        achievementProgress = this.habbo.getHabboStats().getAchievementProgress(achievement);
        level = achievement.getLevelForProgress(achievementProgress);
        nextLevel = achievement.levels.get((level.level > 1) ? level.level + 1 : 1);
        if (nextLevel != null && nextLevel.level == 1)
        {
            level = null;
        }

        this.response.appendInt32(achievement.id); //ID
        this.response.appendInt32(achievementProgress == -1 ? 1 : (nextLevel == null) ? level.level : nextLevel.level); //Target level
        this.response.appendString("ACH_" + achievement.name + ((nextLevel == null) ? level.level : nextLevel.level)); //Target badge code
        this.response.appendInt32(level != null ? level.progress : 0); //Last level progress needed
        this.response.appendInt32(nextLevel != null ? nextLevel.progress : 0); //Progress needed
        this.response.appendInt32(nextLevel != null ? nextLevel.rewardAmount : 0); //Reward amount
        this.response.appendInt32(nextLevel != null ? nextLevel.rewardCurrency : 0); //Reward currency ID
        this.response.appendInt32(achievementProgress == -1 ? 0 : achievementProgress); //Current progress
        this.response.appendBoolean(AchievementManager.hasAchieved(habbo, achievement)); //Achieved? (Current Progress == MaxLevel.Progress)
        this.response.appendString(achievement.category.toString().toLowerCase()); //Category
        this.response.appendString(""); //Empty, completly unused in client code
        this.response.appendInt32(achievement.levels.size()); //Count of total levels in this achievement
        this.response.appendInt32(0); //1 = Progressbar visible if the achievement is completed
        return this.response;
    }
}