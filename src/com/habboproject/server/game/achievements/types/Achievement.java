package com.habboproject.server.game.achievements.types;

public class Achievement {
    private final int level;
    private final int rewardActivityPoints;
    private final int rewardAchievement;
    private final int progressNeeded;

    public Achievement(int level, int rewardActivityPoints, int rewardAchievement, int progressNeeded) {
        this.level = level;
        this.rewardActivityPoints = rewardActivityPoints;
        this.rewardAchievement = rewardAchievement;
        this.progressNeeded = progressNeeded;
    }

    public int getLevel() {
        return level;
    }

    public int getRewardActivityPoints() {
        return rewardActivityPoints;
    }

    public int getRewardAchievement() {
        return rewardAchievement;
    }

    public int getProgressNeeded() {
        return progressNeeded;
    }
}
