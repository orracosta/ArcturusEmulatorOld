package com.habboproject.server.network.messages.outgoing.user.achievements;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.achievements.types.Achievement;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class AchievementUnlockedMessageComposer extends MessageComposer {
    private final String achievementCategory;
    private final String achievementName;

    private final int currentAchievementId;
    private final Achievement newAchievement;

    public AchievementUnlockedMessageComposer(final String achievementCategory, String achievementName, int currentAchievementId, Achievement newAchievement) {
        this.achievementCategory = achievementCategory;
        this.achievementName = achievementName;
        this.currentAchievementId = currentAchievementId;
        this.newAchievement = newAchievement;
    }

    @Override
    public short getId() {
        return Composers.AchievementUnlockedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(currentAchievementId);
        msg.writeInt(newAchievement == null ? 1 : newAchievement.getLevel());
        msg.writeInt(144);
        msg.writeString(achievementName + (newAchievement == null ? 1 : (newAchievement.getLevel() - 1)));
        msg.writeInt(newAchievement == null ? 0 : newAchievement.getRewardAchievement());
        msg.writeInt(newAchievement == null ? 0 : newAchievement.getRewardActivityPoints());
        msg.writeInt(0);
        msg.writeInt(10);
        msg.writeInt(21);
        msg.writeString(newAchievement != null ? newAchievement.getLevel() > 1 ? achievementName + (newAchievement.getLevel() - 1) : "" : "");

        msg.writeString(achievementCategory.toLowerCase());
        msg.writeBoolean(true);
    }
}
