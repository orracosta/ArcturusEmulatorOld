package com.habboproject.server.game.players.components.types.achievements;

public class AchievementProgress {
    private int level;
    private int progress;

    public AchievementProgress(int level, int progress) {
        this.level = level;
        this.progress = progress;
    }

    public void increaseProgress(int amount) {
        this.progress += amount;
    }

    public void decreaseProgress(int difference) {
        this.progress -= difference;
    }

    public void increaseLevel() {
        this.level += 1;
    }

    public int getLevel() {
        return this.level;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
