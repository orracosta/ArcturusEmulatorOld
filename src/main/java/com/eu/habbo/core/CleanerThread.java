package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.friends.SearchUserEvent;
import com.eu.habbo.messages.incoming.navigator.SearchRoomsEvent;
import com.eu.habbo.threading.runnables.AchievementUpdater;
import com.eu.habbo.util.callback.HTTPPostStatus;
import com.eu.habbo.util.callback.HTTPVersionCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CleanerThread implements Runnable {

    /**
     * Delay between each execution of checking to clean up in MS.
     */
    public static final int DELAY = 10000;

    /**
     * Amount of seconds the Hall Of Fame has to be reloaded.
     */
    public static final int RELOAD_HALL_OF_FAME = 1800;

    /**
     * Amount of seconds the News List has to be reloaded.
     */
    public static final int RELOAD_NEWS_LIST = 3600;

    /**
     * Amount of seconds inactive rooms have to be cleared.
     */
    public static final int REMOVE_INACTIVE_ROOMS = 120;

    /**
     * Amount of seconds inactive guilds have to be cleared.
     */
    public static final int REMOVE_INACTIVE_GUILDS = 60;

    /**
     * Amount of seconds inactive tours have to be cleared.
     */
    public static final int REMOVE_INACTIVE_TOURS = 600;

    /**
     * Amount of seconds error logs have to be saved to the database.
     */
    public static final int SAVE_ERROR_LOGS = 30;

    /**
     *
     */
    private static final int CALLBACK_TIME = 60*15;


    /**
     * Last time the Hall Of Fame was reloaded.
     */
    private static int LAST_HOF_RELOAD = Emulator.getIntUnixTimestamp();

    /**
     * Last time the news list was reloaded.
     */
    private static int LAST_NL_RELOAD = Emulator.getIntUnixTimestamp();

    /**
     * Last time inactive rooms have been cleared.
     */
    private static int LAST_INACTIVE_ROOMS_CLEARED = Emulator.getIntUnixTimestamp();

    /**
     * Last time inactive guilds have been cleared.
     */
    private static int LAST_INACTIVE_GUILDS_CLEARED = Emulator.getIntUnixTimestamp();

    /**
     * Last time inactive tours have been cleared.
     */
    private static int LAST_INACTIVE_TOURS_CLEARED = Emulator.getIntUnixTimestamp();

    /**
     * Last time error logs were saved to the database.
     */
    private static int LAST_ERROR_LOGS_SAVED = Emulator.getIntUnixTimestamp();

    /**
     * Last time dailys where refilled.
     */
    private static int LAST_DAILY_REFILL = Emulator.getIntUnixTimestamp();

    private static int LAST_CALLBACK = Emulator.getIntUnixTimestamp();

    public CleanerThread()
    {
        databaseCleanup();
        Emulator.getThreading().run(this, DELAY);

        Emulator.getThreading().run(new AchievementUpdater());

        Emulator.getThreading().run(new HTTPVersionCheck(), 10000);
    }

    @Override
    public void run()
    {
        Emulator.getThreading().run(this, DELAY);

        int time = Emulator.getIntUnixTimestamp();

        if (time - LAST_HOF_RELOAD > RELOAD_HALL_OF_FAME)
        {
            Emulator.getGameEnvironment().getHotelViewManager().getHallOfFame().reload();
            LAST_HOF_RELOAD = time;
        }

        if (time - LAST_NL_RELOAD > RELOAD_NEWS_LIST)
        {
            Emulator.getGameEnvironment().getHotelViewManager().getNewsList().reload();
            LAST_NL_RELOAD = time;
        }

        if (time - LAST_INACTIVE_ROOMS_CLEARED > REMOVE_INACTIVE_ROOMS)
        {
            Emulator.getGameEnvironment().getRoomManager().clearInactiveRooms();
            LAST_INACTIVE_ROOMS_CLEARED = time;
        }

        if (time - LAST_INACTIVE_GUILDS_CLEARED > REMOVE_INACTIVE_GUILDS)
        {
            Emulator.getGameEnvironment().getGuildManager().clearInactiveGuilds();
            Emulator.getGameEnvironment().getGuildForumManager().clearInactiveForums();
            LAST_INACTIVE_GUILDS_CLEARED = time;
        }

        if (time - LAST_INACTIVE_TOURS_CLEARED > REMOVE_INACTIVE_TOURS)
        {
            Emulator.getGameEnvironment().getGuideManager().cleanup();
            LAST_INACTIVE_TOURS_CLEARED = time;
        }

        if (time - LAST_ERROR_LOGS_SAVED > SAVE_ERROR_LOGS)
        {
            Emulator.getLogging().saveLogs();
            LAST_ERROR_LOGS_SAVED = time;
        }

        if (time - LAST_CALLBACK > CALLBACK_TIME)
        {
            Emulator.getThreading().run(new HTTPPostStatus());
            LAST_CALLBACK = time;
        }

        if (time - LAST_DAILY_REFILL > Emulator.getConfig().getInt("hotel.refill.daily"))
        {
            refillDailyRespects();
            LAST_DAILY_REFILL = time;
        }

        SearchRoomsEvent.cachedResults.clear();
        SearchUserEvent.cachedResults.clear();
    }

    /**
     * Cleans up the database before emulator launch to guarantee system integrity.
     */
    void databaseCleanup()
    {
        refillDailyRespects();

        int time = Emulator.getIntUnixTimestamp();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (Statement statement = connection.createStatement())
            {
                statement.execute("UPDATE users SET online = '0' WHERE online = '1'");
                statement.execute("UPDATE rooms SET users = '0' WHERE users > 0");
                statement.execute("DELETE FROM room_mutes WHERE ends < " + time);
                statement.execute("DELETE FROM room_bans WHERE ends < " + time);
                statement.execute("DELETE users_favorite_rooms FROM users_favorite_rooms LEFT JOIN rooms ON room_id = rooms.id WHERE rooms.id IS NULL");
            }

            try (PreparedStatement statement = connection.prepareStatement("UPDATE users_effects SET total = total - 1 WHERE activation_timestamp < ? AND activation_timestamp != 0"))
            {
                statement.setInt(1, Emulator.getIntUnixTimestamp() - 86400);
                statement.execute();
            }

            try (Statement statement = connection.createStatement())
            {
                statement.execute("DELETE FROM users_effects WHERE total <= 0");
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        Emulator.getLogging().logStart("Database -> Cleaned!");
    }

    public void refillDailyRespects()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_settings SET daily_respect_points = ?, daily_pet_respect_points = ?"))
        {
            statement.setInt(1, Emulator.getConfig().getInt("hotel.daily.respect"));
            statement.setInt(2, Emulator.getConfig().getInt("hotel.daily.respect.pets"));
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if (Emulator.isReady)
        {
            for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values())
            {
                habbo.getHabboStats().petRespectPointsToGive = Emulator.getConfig().getInt("hotel.daily.respect");
                habbo.getHabboStats().respectPointsToGive = Emulator.getConfig().getInt("hotel.daily.respect.pets");
            }
        }
    }
}
