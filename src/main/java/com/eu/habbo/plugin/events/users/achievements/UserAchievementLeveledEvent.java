package com.eu.habbo.plugin.events.users.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserAchievementLeveledEvent extends UserAchievementEvent
{
    /**
     * The previous level of the Achievement.
     */
    public final AchievementLevel oldLevel;

    /**
     * The new level of the Achievement.
     */
    public final AchievementLevel newLevel;

    /**
     * @param habbo       The Habbo this event applies to.
     * @param achievement The Achievement this event applies to.
     * @param oldLevel    The previous level of the Achievement.
     * @param newLevel    The new level of the Achievement.
     */
    public UserAchievementLeveledEvent(Habbo habbo, Achievement achievement, AchievementLevel oldLevel, AchievementLevel newLevel)
    {
        super(habbo, achievement);

        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }
}
