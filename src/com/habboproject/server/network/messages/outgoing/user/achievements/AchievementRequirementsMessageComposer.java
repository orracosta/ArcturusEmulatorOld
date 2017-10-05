package com.habboproject.server.network.messages.outgoing.user.achievements;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.achievements.AchievementGroup;
import com.habboproject.server.game.achievements.AchievementManager;
import com.habboproject.server.game.achievements.types.Achievement;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class AchievementRequirementsMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.BadgeDefinitionsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(AchievementManager.getInstance().getAchievementGroups().size());

        for (AchievementGroup achievementGroup : AchievementManager.getInstance().getAchievementGroups().values()) {
            msg.writeString(achievementGroup.getGroupName().replace("ACH_", ""));
            msg.writeInt(achievementGroup.getAchievements().size());

            for (Achievement achievement : achievementGroup.getAchievements().values()) {
                msg.writeInt(achievement.getLevel());
                msg.writeInt(achievement.getProgressNeeded());
            }
        }
    }
}
