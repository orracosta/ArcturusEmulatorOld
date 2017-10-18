package com.eu.habbo.plugin.events.users.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserAchievementProgressEvent extends UserAchievementEvent
{
    /**
     * The amount of progress that has been gained.
     */
    public final int progressed;

    /**
     * @param habbo        The Habbo this event applies to.
     * @param achievement  The Achievement this event applies to.
     * @param progressed   The amount of progress that has been gained.
     */
    public UserAchievementProgressEvent(Habbo habbo, Achievement achievement, int progressed)
    {
        super(habbo, achievement);

        this.progressed = progressed;
    }
}
