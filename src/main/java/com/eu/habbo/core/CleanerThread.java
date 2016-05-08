package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.util.callback.HTTPPostStatus;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 26-8-2014 11:48.
 */
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

    private static int LAST_CALLBACK = Emulator.getIntUnixTimestamp();

    public CleanerThread()
    {
        databaseCleanup();
        Emulator.getThreading().run(this, DELAY);
    }

    @Override
    public void run()
    {
        Emulator.getThreading().run(this, DELAY);

        int time = Emulator.getIntUnixTimestamp();

        if(time - LAST_HOF_RELOAD > RELOAD_HALL_OF_FAME)
        {
            Emulator.getGameEnvironment().getHotelViewManager().getHallOfFame().reload();
            LAST_HOF_RELOAD = time;
        }

        if(time - LAST_NL_RELOAD > RELOAD_NEWS_LIST)
        {
            Emulator.getGameEnvironment().getHotelViewManager().getNewsList().reload();
            LAST_NL_RELOAD = time;
        }

        if(time - LAST_INACTIVE_ROOMS_CLEARED > REMOVE_INACTIVE_ROOMS)
        {
            Emulator.getGameEnvironment().getRoomManager().clearInactiveRooms();
            LAST_INACTIVE_ROOMS_CLEARED = time;
        }

        if(time - LAST_INACTIVE_GUILDS_CLEARED > REMOVE_INACTIVE_GUILDS)
        {
            Emulator.getGameEnvironment().getGuildManager().clearInactiveGuilds();
            LAST_INACTIVE_GUILDS_CLEARED = time;
        }

        if(time - LAST_INACTIVE_TOURS_CLEARED > REMOVE_INACTIVE_TOURS)
        {
            Emulator.getGameEnvironment().getGuideManager().cleanup();
            LAST_INACTIVE_TOURS_CLEARED = time;
        }

        if(time - LAST_ERROR_LOGS_SAVED > SAVE_ERROR_LOGS)
        {
            Emulator.getLogging().saveLogs();
            LAST_ERROR_LOGS_SAVED = time;
        }

        if(time - LAST_CALLBACK > CALLBACK_TIME)
        {
            Emulator.getThreading().run(new HTTPPostStatus());
            LAST_CALLBACK = time;
        }
    }

    /**
     * Cleans up the database before emulator launch to guarantee system integrity.
     */
    void databaseCleanup()
    {
        long millis = System.currentTimeMillis();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users SET online = ?");
            statement.setString(1, "0");
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        Emulator.getLogging().logStart("Database -> Cleaned! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
}
