package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

public class ProgressAchievement extends RCONMessage<ProgressAchievement.ProgressAchievementJSON>
{
    /**
     * Progresses an achievement for an ONLINE user.
     * Achievements for offline users are not managed by the emulator.
     */
    public ProgressAchievement()
    {
        super(ProgressAchievementJSON.class);
    }

    @Override
    public void handle(Gson gson, ProgressAchievementJSON json)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        if (habbo != null)
        {
            Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement(json.achievement_id);
            if (achievement != null)
            {
                AchievementManager.progressAchievement(habbo, achievement, json.progress);
            }
            else
            {
                this.status = RCONMessage.STATUS_ERROR;
            }
        }
        else
        {
            this.status = RCONMessage.HABBO_NOT_FOUND;
        }
    }

    public class ProgressAchievementJSON
    {
        public int user_id;
        public int achievement_id;
        public int progress;
    }
}