package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;

public class InventoryAchievementsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.InventoryAchievementsComposer);

        synchronized (Emulator.getGameEnvironment().getAchievementManager().getAchievements())
        {
            THashMap<String, Achievement> achievements = Emulator.getGameEnvironment().getAchievementManager().getAchievements();

            this.response.appendInt(achievements.size());
            achievements.forEachValue(new TObjectProcedure<Achievement>()
            {
                @Override
                public boolean execute(Achievement achievement)
                {
                    response.appendString((achievement.name.startsWith("ACH_") ? achievement.name.replace("ACH_", "") : achievement.name));
                    response.appendInt(achievement.levels.size());

                    for (AchievementLevel level : achievement.levels.values())
                    {
                        response.appendInt(level.level);
                        response.appendInt(level.progress);
                    }

                    return true;
                }
            });
        }
        return this.response;
    }
}
