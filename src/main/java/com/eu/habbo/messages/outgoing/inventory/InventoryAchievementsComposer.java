package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 30-1-2015 20:04.
 */
public class InventoryAchievementsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.InventoryAchievementsComposer);

        synchronized (Emulator.getGameEnvironment().getAchievementManager().achievements)
        {
            this.response.appendInt32(Emulator.getGameEnvironment().getAchievementManager().achievements.size());

            for (Achievement achievement : Emulator.getGameEnvironment().getAchievementManager().achievements.values())
            {
                this.response.appendString((achievement.name.startsWith("ACH_") ? achievement.name.replace("ACH_", "") : achievement.name));
                this.response.appendInt32(achievement.levels.size());

                for (AchievementLevel level : achievement.levels.values())
                {
                    this.response.appendInt32(level.level);
                    this.response.appendInt32(level.progress);
                }
            }
        }
        return this.response;
    }
}
