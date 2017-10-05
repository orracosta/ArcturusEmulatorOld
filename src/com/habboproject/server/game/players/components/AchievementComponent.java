package com.habboproject.server.game.players.components;

import com.habboproject.server.game.achievements.AchievementGroup;
import com.habboproject.server.game.achievements.AchievementManager;
import com.habboproject.server.game.achievements.types.Achievement;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.players.components.types.achievements.AchievementProgress;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.players.types.PlayerComponent;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementPointsMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementProgressMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementUnlockedMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.storage.queries.achievements.PlayerAchievementDao;

import java.util.Map;

public class AchievementComponent implements PlayerComponent {
    private final Player player;
    private Map<AchievementType, AchievementProgress> progression;

    public AchievementComponent(Player player) {
        this.player = player;

        this.loadAchievements();
    }

    public void loadAchievements() {
        if (this.progression != null) {
            this.progression.clear();
        }

        this.progression = PlayerAchievementDao.getAchievementProgress(this.player.getId());
    }

    public void progressAchievement(AchievementType type, int data) {
        AchievementGroup achievementGroup = AchievementManager.getInstance().getAchievementGroup(type);

        if (achievementGroup == null) {
            return;
        }

        AchievementProgress progress;

        if (this.progression.containsKey(type)) {
            progress = this.progression.get(type);
        } else {
            progress = new AchievementProgress(1, 0);
            this.progression.put(type, progress);
        }

        if (achievementGroup.getAchievement(progress.getLevel()) == null)
            return;

        if (achievementGroup.getAchievements() == null)
            return;

        if (achievementGroup.getAchievements().size() <= progress.getLevel() && achievementGroup.getAchievement(progress.getLevel()).getProgressNeeded() <= progress.getProgress()) {
            return;
        }

        final int targetLevel = progress.getLevel() + 1;
        final Achievement currentAchievement = achievementGroup.getAchievement(progress.getLevel());
        final Achievement targetAchievement = achievementGroup.getAchievement(targetLevel);

        if (targetAchievement == null && achievementGroup.getLevelCount() != 1) {

            progress.setProgress(currentAchievement.getProgressNeeded());
            PlayerAchievementDao.saveProgress(this.player.getId(), type, progress);

            this.player.getData().save();
            this.player.getInventory().achievementBadge(type.getGroupName(), currentAchievement.getLevel());
            return;
        }

        int progressToGive = currentAchievement.getProgressNeeded() <= data ? currentAchievement.getProgressNeeded() : data;
        int remainingProgress = progressToGive >= data ? 0 : data - progressToGive;

        progress.increaseProgress(progressToGive);

        if (progress.getProgress() > currentAchievement.getProgressNeeded()) {
            // subtract the difference and add it onto remainingProgress.
            int difference = progress.getProgress() - currentAchievement.getProgressNeeded();

            progress.decreaseProgress(difference);
            remainingProgress += difference;
        }

        if (currentAchievement.getProgressNeeded() <= progress.getProgress()) {
            this.player.getData().increaseAchievementPoints(currentAchievement.getRewardAchievement());
            this.player.getData().increaseActivityPoints(currentAchievement.getRewardActivityPoints());

            this.player.poof();

            this.getPlayer().getSession().send(this.getPlayer().composeCurrenciesBalance());
            this.getPlayer().getSession().send(new UpdateActivityPointsMessageComposer(this.getPlayer().getData().getActivityPoints(), currentAchievement.getRewardAchievement()));

            if (achievementGroup.getAchievement(targetLevel) != null) {
                progress.increaseLevel();
            }

            // Achievement unlocked!
            this.player.getSession().send(new AchievementPointsMessageComposer(this.getPlayer().getData().getAchievementPoints()));
            this.player.getSession().send(new AchievementProgressMessageComposer(progress, achievementGroup));
            this.player.getSession().send(new AchievementUnlockedMessageComposer(achievementGroup.getCategory().toString(), achievementGroup.getGroupName(), achievementGroup.getId(), targetAchievement));

            this.player.getInventory().achievementBadge(type.getGroupName(), currentAchievement.getLevel());
        } else {
            this.player.getSession().send(new AchievementProgressMessageComposer(progress, achievementGroup));
        }

        boolean hasFinishedGroup = false;

        if(progress.getLevel() >= achievementGroup.getLevelCount() && progress.getProgress() >= achievementGroup.getAchievement(achievementGroup.getLevelCount()).getProgressNeeded()) {
            hasFinishedGroup = true;
        }

        if (remainingProgress != 0 && !hasFinishedGroup) {
            this.progressAchievement(type, remainingProgress);
            return;
        }

        this.player.getData().save();
        PlayerAchievementDao.saveProgress(this.player.getId(), type, progress);
    }

    public boolean hasStartedAchievement(AchievementType achievementType) {
        return this.progression.containsKey(achievementType);
    }

    public AchievementProgress getProgress(AchievementType achievementType) {
        return this.progression.get(achievementType);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void dispose() {
        this.progression.clear();
    }
}
