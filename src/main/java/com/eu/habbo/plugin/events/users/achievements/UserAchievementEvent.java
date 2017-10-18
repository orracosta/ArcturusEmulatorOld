package com.eu.habbo.plugin.events.users.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public abstract class UserAchievementEvent extends UserEvent
{
    /**
     * The Achievement this event applies to.
     */
    public final Achievement achievement;

    /**
     * @param habbo The Habbo this event applies to.
     * @param achievement The Achievement this event applies to.
     */
    public UserAchievementEvent(Habbo habbo, Achievement achievement)
    {
        super(habbo);

        this.achievement = achievement;
    }
}
