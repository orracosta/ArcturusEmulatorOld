package com.eu.habbo.habbohotel.achievements;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.achievements.AchievementProgressComposer;
import com.eu.habbo.messages.outgoing.achievements.AchievementUnlockedComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.users.UserBadgesComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.users.achievements.UserAchievementLeveledEvent;
import com.eu.habbo.plugin.events.users.achievements.UserAchievementProgressEvent;
import gnu.trove.map.hash.THashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AchievementManager
{
    /**
     * All the achievements in the hotel are stored in this map where:
     * String = name of the achievement (Without ACH_ & Roman number.
     * Achievement = Instance of the Achievement class.
     */
    private final THashMap<String, Achievement> achievements;

    /**
     * The AchievementManager, shit happens here.
     */
    public AchievementManager()
    {
        long millis = System.currentTimeMillis();
        this.achievements = new THashMap<String, Achievement>();
        this.reload();
        Emulator.getLogging().logStart("Achievement Manager -> Loaded! ("+(System.currentTimeMillis() - millis)+" MS)");
    }

    /**
     * Reloads the achievement manager.
     */
    public void reload()
    {
        synchronized (this.achievements)
        {
            this.achievements.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM achievements");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    if (!this.achievements.containsKey(set.getString("name")))
                    {
                        this.achievements.put(set.getString("name"), new Achievement(set));
                    } else
                    {
                        this.achievements.get(set.getString("name")).addLevel(new AchievementLevel(set));
                    }
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            } catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    /**
     * @param name The achievement to find.
     * @return The achievement
     */
    public Achievement getAchievement(String name)
    {
        return this.achievements.get(name);
    }

    /**
     * Progresses an Habbo's achievement by 1.
     * @param habbo The Habbo whose achievement should be progressed.
     * @param achievement The Achievement to be progressed.
     */
    public static void progressAchievement(Habbo habbo, Achievement achievement)
    {
        progressAchievement(habbo, achievement, 1);
    }

    /**
     * Progresses an Habbo's achievement by an given amount.
     * @param habbo The Habbo whose achievement should be progressed.
     * @param achievement The Achievement to be progressed.
     * @param amount The amount that should be progressed.
     */
    public static void progressAchievement(Habbo habbo, Achievement achievement, int amount)
    {
        if(achievement == null)
            return;

        if(habbo == null)
            return;

        int currentProgress = habbo.getHabboStats().getAchievementProgress(achievement);

        if(currentProgress == -1)
        {
            currentProgress = 0;
            createUserEntry(habbo, achievement);
            habbo.getHabboStats().setProgress(achievement, 0);
        }

        if(Emulator.getPluginManager().isRegistered(UserAchievementProgressEvent.class, true))
        {
            Event userAchievementProgressedEvent = new UserAchievementProgressEvent(habbo, achievement, amount);
            Emulator.getPluginManager().fireEvent(userAchievementProgressedEvent);

            if(userAchievementProgressedEvent.isCancelled())
                return;
        }

        AchievementLevel oldLevel = achievement.getLevelForProgress(currentProgress);

        if(oldLevel == null)
            return;

        if(oldLevel.level == achievement.levels.size() && currentProgress == oldLevel.progress)
            return;

        habbo.getHabboStats().setProgress(achievement, currentProgress + amount);

        AchievementLevel newLevel = achievement.getLevelForProgress(currentProgress + amount);

        if(oldLevel.level == newLevel.level)
        {
            habbo.getClient().sendResponse(new AchievementProgressComposer(habbo, achievement));
        }
        else
        {
            if(Emulator.getPluginManager().isRegistered(UserAchievementLeveledEvent.class, true))
            {
                Event userAchievementLeveledEvent = new UserAchievementLeveledEvent(habbo, achievement, oldLevel, newLevel);
                Emulator.getPluginManager().fireEvent(userAchievementLeveledEvent);

                if(userAchievementLeveledEvent.isCancelled())
                    return;
            }

            habbo.getClient().sendResponse(new AchievementProgressComposer(habbo, achievement));
            habbo.getClient().sendResponse(new AchievementUnlockedComposer(habbo, achievement));

            HabboBadge badge = habbo.getHabboInventory().getBadgesComponent().getBadge("ACH_" + achievement.name + oldLevel.level);

            if (badge != null)
            {
                badge.setCode("ACH_" + achievement.name + newLevel.level);
                badge.needsInsert(false);
                badge.needsUpdate(true);
            } else
            {
                badge = new HabboBadge(0, "ACH_" + achievement.name + newLevel.level, 0, habbo);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));
                badge.needsInsert(true);
                badge.needsUpdate(true);
            }

            Emulator.getThreading().run(badge);

            if(badge.getSlot() > 0)
            {
                if(habbo.getHabboInfo().getCurrentRoom() != null)
                {
                    habbo.getHabboInfo().getCurrentRoom().sendComposer(new UserBadgesComposer(habbo.getHabboInventory().getBadgesComponent().getWearingBadges(), habbo.getHabboInfo().getId()).compose());
                }
            }

            habbo.getHabboStats().addAchievementScore(newLevel.points);
        }
    }

    /**
     * Checks wether the given Habbo has achieved a certain Achievement.
     * @param habbo The Habbo to check.
     * @param achievement The Achievement to check.
     * @return True when the given Habbo has achieved the Achievement.
     */
    public static boolean hasAchieved(Habbo habbo, Achievement achievement)
    {
        int currentProgress = habbo.getHabboStats().getAchievementProgress(achievement);

        if(currentProgress == -1)
        {
            return false;
        }

        AchievementLevel level = achievement.getLevelForProgress(currentProgress);

        if (level == null)
        {
            return false;
        }

        return level.progress <= currentProgress;
    }

    /**
     * Creates an new Achievement entry in the database.
     * @param habbo The Habbo the achievement should be saved for.
     * @param achievement The Achievement that should be inserted.
     */
    public static void createUserEntry(Habbo habbo, Achievement achievement)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO users_achievements (user_id, achievement_name, progress) VALUES (?, ?, ?)");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setString(2, achievement.name);
            statement.setInt(3, 1);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Saves all the Achievements for the given Habbo to the database.
     * @param habbo The Habbo whose Achievements should be saved.
     */
    public static void saveAchievements(Habbo habbo)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_achievements SET progress = ? WHERE achievement_name = ? AND user_id = ?");

            statement.setInt(3, habbo.getHabboInfo().getId());
            for(Map.Entry<Achievement, Integer> map : habbo.getHabboStats().getAchievementProgress().entrySet())
            {
                statement.setInt(1, map.getValue());
                statement.setString(2, map.getKey().name);
                statement.execute();
            }

            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
