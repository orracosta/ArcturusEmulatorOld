package com.eu.habbo.habbohotel.achievements;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 30-1-2015 14:32.
 */
public class AchievementLevel
{
    public final int level;
    public final int pixels;
    public final int points;
    public final int progress;

    public AchievementLevel(ResultSet set) throws SQLException
    {
        this.level = set.getInt("level");
        this.pixels = set.getInt("pixels");
        this.points = set.getInt("points");
        this.progress = set.getInt("progress_needed");
    }
}
