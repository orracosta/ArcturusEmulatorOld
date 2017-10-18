package com.eu.habbo.habbohotel.achievements;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TalentTrackLevel
{
    /**
     * Type of this talen track level.
     */
    public TalentTrackType type;

    /**
     * The level of this TalenTrackLevel.
     */
    public int level;

    /**
     * Achievements required to achieve this level.
     */
    public TObjectIntMap<Achievement> achievements;

    /**
     * Items that will be rewarded upon achieving this level.
     */
    public THashSet<Item> items;

    /**
     * Perks unlocked upon achieving this level.
     */
    public String[] perks;

    /**
     * Badges received upon achieving this level.
     */
    public String[] badges;

    public TalentTrackLevel(ResultSet set) throws SQLException
    {
        this.type         = TalentTrackType.valueOf(set.getString("type").toUpperCase());
        this.level        = set.getInt("level");
        this.achievements = new TObjectIntHashMap<Achievement>();
        this.items        = new THashSet<Item>();

        String[] achievements      = set.getString("achievement_ids").split(",");
        String[] achievementLevels = set.getString("achievement_levels").split(",");
        if (achievementLevels.length == achievements.length)
        {
            for (int i = 0; i < achievements.length; i++)
            {
                if (achievements[i].isEmpty() || achievementLevels[i].isEmpty())
                    continue;

                Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement(Integer.valueOf(achievements[i]));

                if (achievement != null)
                {
                    this.achievements.put(achievement, Integer.valueOf(achievementLevels[i]));
                }
                else
                {
                    Emulator.getLogging().logErrorLine("Could not find achievement with ID " + achievements[i] + " for talenttrack level " + this.level + " of type " + this.type);
                }
            }
        }

        for (String s : set.getString("reward_furni").split(","))
        {
            Item item = Emulator.getGameEnvironment().getItemManager().getItem(Integer.valueOf(s));

            if (item != null)
            {
                items.add(item);
            }
            else
            {
                Emulator.getLogging().logStart("Incorrect reward furni (ID: " + s + ") for talent track level " + this.level);
            }
        }

        this.perks  = set.getString("reward_perks").split(",");
        this.badges = set.getString("reward_badges").split(",");
    }
}