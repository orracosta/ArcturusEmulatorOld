package com.habboproject.server.game.achievements;

import com.habboproject.server.game.achievements.types.Achievement;
import com.habboproject.server.game.achievements.types.AchievementCategory;

import java.util.Map;

public class AchievementGroup {
    private Map<Integer, Achievement> achievements;

    private int id;
    private String groupName;
    private AchievementCategory category;

    public AchievementGroup(int id, Map<Integer, Achievement> achievements, String groupName, AchievementCategory category) {
        this.id = id;
        this.achievements = achievements;
        this.groupName = groupName;
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public int getLevelCount() {
        return this.achievements.size();
    }

    public Achievement getAchievement(int level) {
        return this.achievements.get(level);
    }

    public Map<Integer, Achievement> getAchievements() {
        return achievements;
    }

    public String getGroupName() {
        return groupName;
    }

    public AchievementCategory getCategory() {
        return category;
    }
}
