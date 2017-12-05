package com.eu.habbo.habbohotel.achievements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AchievementLevel
{
    /**
     * level of the achievement.
     */
    public final int level;

    /**
     * Amount of currency to give upon achieving this level.
     */
    public final int rewardAmount;

    /**
     * Currency type to give upon achieving this level.
     */
    public final int rewardType;

    /**
     * Amount of achievement points to add upon achieving this level.
     */
    public final int points;

    /**
     * Amount of progress needed to achieve this level.
     */
    public final int progress;

    public AchievementLevel(ResultSet set) throws SQLException
    {
        this.level        = set.getInt("level");
        this.rewardAmount = set.getInt("reward_amount");
        this.rewardType = set.getInt("reward_type");
        this.points       = set.getInt("points");
        this.progress     = set.getInt("progress_needed");
    }
}
