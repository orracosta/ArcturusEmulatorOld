package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 30-1-2015 13:56.
 */
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

        int achievementProgress = this.habbo.getHabboStats().getAchievementProgress(achievement);
        AchievementLevel level = achievement.getLevelForProgress(achievementProgress);
        AchievementLevel oldLevel = achievement.levels.get((level != null) ? level.level - 1 : -1);

        this.response.appendInt32(achievement.id); //ID
        this.response.appendInt32(achievementProgress == -1 ? 1 : (level == null) ? 1 : level.level);
        this.response.appendString("ACH_" + achievement.name + ((level == null ? 0 : level.level) + 1));
        this.response.appendInt32(oldLevel != null ? oldLevel.progress : 0);
        this.response.appendInt32(level != null ? level.progress : 0);
        this.response.appendInt32(level != null ? level.pixels : 0);
        this.response.appendInt32(0);
        this.response.appendInt32(achievementProgress == -1 ? 0 : this.habbo.getHabboStats().getAchievementProgress(achievement)); //Current Progress
        this.response.appendBoolean(AchievementManager.hasAchieved(habbo, achievement)); //Achieved? (Current Progress == Level.Progress)
        this.response.appendString(achievement.category.toString().toLowerCase());
        this.response.appendString("100");
        this.response.appendInt32(achievement.levels.size()); //Count of total levels in this achievement.
        this.response.appendInt32(0); //IDK
        return this.response;
    }
}
