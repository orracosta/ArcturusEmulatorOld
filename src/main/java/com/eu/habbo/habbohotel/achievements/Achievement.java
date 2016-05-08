package com.eu.habbo.habbohotel.achievements;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 29-12-2014 16:09.
 */
public class Achievement
{
    /**
     * Id of the Achievement.
     */
    public final int id;

    /**
     * Name of the Achievement;
     */
    public final String name;

    /**
     * Category of the achievement.
     */
    public final AchievementCategories category;

    /**
     * The levels this achievement has.
     */
    public final THashMap<Integer, AchievementLevel> levels;

    /**
     * Creates an new achievement.
     * @param set The ResultSet it should fetch the data from.
     * @throws SQLException
     */
    public Achievement(ResultSet set) throws SQLException
    {
        levels = new THashMap<Integer, AchievementLevel>();

        id = set.getInt("id");
        this.name = set.getString("name");
        this.category = AchievementCategories.valueOf(set.getString("category").toUpperCase());

        this.addLevel(new AchievementLevel(set));
    }

    /**
     * Adds an new AchievementLevel to the Achievement.
     * @param level The AchievementLevel to be added.
     */
    public void addLevel(AchievementLevel level)
    {
        synchronized (this.levels)
        {
            this.levels.put(level.level, level);
        }
    }

    /**
     * Calculates the AchievementLevel for the given progress.
     * @param progress The amount of progress.
     * @return The AchievementLevel that matches the amount of progress.
     */
    public AchievementLevel getLevelForProgress(int progress)
    {
        AchievementLevel l = null;

        for(AchievementLevel level : this.levels.values())
        {
            if(level.progress >= progress && (l == null || level.level < l.level))
            {
                l = level;
            }
        }

        return l;
    }
}
