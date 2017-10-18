package com.eu.habbo;

import com.eu.habbo.core.*;
import com.eu.habbo.core.consolecommands.ConsoleCommand;
import com.eu.habbo.database.Database;
import com.eu.habbo.habbohotel.GameEnvironment;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.gameserver.GameServer;
import com.eu.habbo.networking.rconserver.RCONServer;
import com.eu.habbo.plugin.PluginManager;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStartShutdownEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStoppedEvent;
import com.eu.habbo.threading.ThreadPooling;
import com.eu.habbo.threading.runnables.CameraClientAutoReconnect;
import com.eu.habbo.util.imager.badges.BadgeImager;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public final class Emulator
{
    /**
     * Major version of the emulator.
     */
    public final static int MAJOR = 1;

    /**
     * Minor version of the emulator.
     */
    public final static int MINOR = 10;

    /**
     * Stable build version of the emulator.
     */
    public final static int BUILD = 0;

    /**
     * Version as string.
     */
    public static final String version = "Version: " + MAJOR + "." + MINOR + "." + BUILD;

    /**
     * Public chat, used for staffchat for now.
     */
    public static MessengerBuddy publicChatBuddy;

    /**
     * The emulator is fully loaded and ready to handle players.
     */
    public static boolean isReady = false;

    /**
     * The emulator is shutting down.
     */
    public static boolean isShuttingDown = false;

    /**
     * The emulator has fully shutdown.
     */
    public static boolean stopped = false;

    /**
     * The emulator is in debugging mode.
     */
    public static boolean debugging = false;

    private static int                      timeStarted = 0;
    private static Runtime                  runtime;
    private static ConfigurationManager     config;
    private static TextsManager             texts;
    private static GameServer               gameServer;
    private static RCONServer               rconServer;
    private static CameraClient             cameraClient;
    private static Database                 database;
    private static Logging                  logging;
    private static ThreadPooling            threading;
    private static GameEnvironment          gameEnvironment;
    private static PluginManager            pluginManager;
    private static Random                   random;
    private static BadgeImager              badgeImager;
    
    static
    {
        Thread hook = new Thread(new Runnable()
        {
            public synchronized void run()
            {
                Emulator.dispose();
            }
        });
        hook.setPriority(10);
        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * Entry point for Arcturus Emulator.
     * @param args Unused
     * @throws Exception Failed to start the emulator.
     */
    public static void main(String[] args) throws Exception
    {
        try
        {
            Emulator.stopped = false;
            ConsoleCommand.load();
            Emulator.logging = new Logging();
            Emulator.getLogging().logStart("\r" + Emulator.logo);
            random = new Random();
            publicChatBuddy = new MessengerBuddy(-1, "Staff Chat", "", (short) 0, 0);
            long startTime = System.nanoTime();

            Emulator.runtime = Runtime.getRuntime();
            Emulator.config = new ConfigurationManager("config.ini");

            if (Emulator.getConfig().getValue("username").isEmpty())
            {
                Emulator.getLogging().logErrorLine("Please make sure you enter your forum login details!");
                Thread.sleep(2000);
            }

            Emulator.database = new Database(Emulator.getConfig());
            Emulator.config.loaded = true;
            Emulator.config.loadFromDatabase();
            Emulator.threading = new ThreadPooling(Emulator.getConfig().getInt("runtime.threads"));
            Emulator.getDatabase().getDataSource().setMaximumPoolSize(Emulator.getConfig().getInt("runtime.threads") * 2);
            Emulator.getDatabase().getDataSource().setMinimumIdle(10);
            Emulator.pluginManager = new PluginManager();
            Emulator.pluginManager.reload();
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
            Emulator.texts = new TextsManager();
            new CleanerThread();
            Emulator.gameServer = new GameServer(getConfig().getValue("game.host", "127.0.0.1"), getConfig().getInt("game.port", 30000));
            Emulator.rconServer = new RCONServer(getConfig().getValue("rcon.host", "127.0.0.1"), getConfig().getInt("rcon.port", 30001));
            Emulator.gameEnvironment = new GameEnvironment();
            Emulator.gameEnvironment.load();
            Emulator.gameServer.initialise();
            Emulator.gameServer.connect();
            Emulator.rconServer.initialise();
            Emulator.rconServer.connect();
            Emulator.badgeImager = new BadgeImager();
            if (Emulator.getConfig().getBoolean("camera.enabled"))
            {
                Emulator.getThreading().run(new CameraClientAutoReconnect());
            }

            Emulator.getLogging().logStart("Habbo Hotel Emulator has succesfully loaded.");
            Emulator.getLogging().logStart("You're running: " + Emulator.version);
            Emulator.getLogging().logStart("System launched in: " + (System.nanoTime() - startTime) / 1e6 + "ms. Using: " + (Runtime.getRuntime().availableProcessors() * 2) + " threads!");
            Emulator.getLogging().logStart("Memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "/" + (runtime.freeMemory()) / (1024 * 1024) + "MB");

            Emulator.debugging = Emulator.getConfig().getBoolean("debug.mode");

            if (debugging)
            {
                Emulator.getLogging().logDebugLine("Debugging Enabled!");
            }

            Emulator.getPluginManager().fireEvent(new EmulatorLoadedEvent());
            Emulator.isReady = true;
            Emulator.timeStarted = getIntUnixTimestamp();

            if (Emulator.getConfig().getInt("runtime.threads") < (Runtime.getRuntime().availableProcessors() * 2))
            {
                Emulator.getLogging().logStart("Emulator settings runtime.threads (" + Emulator.getConfig().getInt("runtime.threads") + ") can be increased to " + (Runtime.getRuntime().availableProcessors() * 2) + " to possibly increase performance.");
            }

            if (Emulator.getConfig().getValue("username").isEmpty())
            {
                Emulator.getLogging().logErrorLine("No account has been found in config.ini Please create an account on Arcturus.wf and edit the config.ini in order to maximize usage of Arcturus! http://arcturus.wf");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (!isShuttingDown && isReady)
            {
                try
                {
                    String line = reader.readLine();

                    if (line != null)
                    {
                        ConsoleCommand.handle(line);
                    }
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Shutsdown the emulator.
     */
    private static void dispose()
    {
        Emulator.isShuttingDown = true;
        Emulator.isReady = false;
        Emulator.getLogging().logShutdownLine("Stopping Arcturus Emulator " + version + "...");

        try
        {
            if (Emulator.getPluginManager() != null)
                Emulator.getPluginManager().fireEvent(new EmulatorStartShutdownEvent());
        }
        catch (Exception e) {}

        try
        {
            if (Emulator.cameraClient != null)
                Emulator.cameraClient.disconnect();
        }
        catch (Exception e) {}

        try
        {
            if (Emulator.rconServer != null)
                Emulator.rconServer.stop();
        }
        catch (Exception e) {}


        try
        {
            if (Emulator.gameEnvironment != null)
                Emulator.gameEnvironment.dispose();
        }
        catch (Exception e) {}

        try
        {
            if (Emulator.getPluginManager() != null)
                Emulator.getPluginManager().fireEvent(new EmulatorStoppedEvent());
        }
        catch (Exception e) {}

        try
        {
            if (Emulator.pluginManager != null)
                Emulator.pluginManager.dispose();
        }
        catch (Exception e) {}

        Emulator.getLogging().saveLogs();

        try
        {
            if (Emulator.config != null)
            {
                Emulator.config.saveToDatabase();
            }
        }
        catch (Exception e) {}

        try
        {
            if (Emulator.gameServer != null)
                Emulator.gameServer.stop();
        }
        catch (Exception e) {}

        Emulator.getLogging().logShutdownLine("Stopped Arcturus Emulator " + version + "...");

        if (Emulator.database != null)
        {
            Emulator.getDatabase().dispose();
        }
        Emulator.stopped = true;

        try
        {
            if (Emulator.threading != null)
                Emulator.threading.shutDown();
        }
        catch (Exception e) {}
    }

    /**
     * @return The ConfigurationManager
     */
    public static ConfigurationManager getConfig()
    {
        return config;
    }

    /**
     * @return The TextsManager
     */
    public static TextsManager getTexts()
    {
        return texts;
    }

    /**
     * @return The Database
     */
    public static Database getDatabase()
    {
        return database;
    }

    /**
     * @return Current Runtime
     */
    public static Runtime getRuntime()
    {
        return runtime;
    }

    /**
     * @return The GameServer
     */
    public static GameServer getGameServer()
    {
        return gameServer;
    }

    /**
     * @return The RCONServer
     */
    public static RCONServer getRconServer()
    {
        return rconServer;
    }

    /**
     * @return Logging module
     */
    public static Logging getLogging()
    {
        return logging;
    }

    /**
     * @return The ThreadPooling
     */
    public static ThreadPooling getThreading()
    {
        return threading;
    }

    /**
     * @return The GameEnvironment
     */
    public static GameEnvironment getGameEnvironment()
    {
        return gameEnvironment;
    }

    /**
     *
     * @return The PluginManager
     */
    public static PluginManager getPluginManager()
    {
        return pluginManager;
    }

    public static Random getRandom()
    {
        return random;
    }

    public static BadgeImager getBadgeImager()
    {
        return badgeImager;
    }

    public static CameraClient getCameraClient()
    {
        return cameraClient;
    }

    public static synchronized void setCameraClient(CameraClient client)
    {
        cameraClient = client;
    }
    
    public static int getTimeStarted()
    {
        return timeStarted;
    }
    
    public static void prepareShutdown()
    {
        System.exit(0);
    }

    /**
     * Converts a date to an unix timestamp
     * @param date The date to convert
     * @return Return unix timestamp in seconds.
     */
    private static String dateToUnixTimestamp(Date date)
    {
        String res = "";
        Date aux = stringToDate("1970-01-01 00:00:00");
        Timestamp aux1 = dateToTimeStamp(aux);
        Timestamp aux2 = dateToTimeStamp(date);
        long difference = aux2.getTime() - aux1.getTime();
        long seconds = difference / 1000L;
        return res + seconds;
    }

    /**
     * Converts a String to a Date object
     * @param date The String to parse
     * @return The Date of the string.
     */
    private static Date stringToDate(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date res = null;
        try
        {
            res = format.parse(date);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
        return res;
    }
    
    public static Timestamp dateToTimeStamp(Date date)
    {
        return new Timestamp(date.getTime());
    }

    public static Date getDate()
    {
        return new Date(System.currentTimeMillis());
    }

    public static String getUnixTimestamp()
    {
        return dateToUnixTimestamp(getDate());
    }
    
    public static int getIntUnixTimestamp()
    {
        return (int) (System.currentTimeMillis() / 1000);
    }
    
    public static boolean isNumeric(String string)
        throws IllegalArgumentException
    {
        boolean isnumeric = false;
        if ((string != null) && (!string.equals("")))
        {
            isnumeric = true;
            char[] chars = string.toCharArray();
            for (char aChar : chars)
            {
                isnumeric = Character.isDigit(aChar);
                if (!isnumeric)
                {
                    break;
                }
            }
        }
        return isnumeric;
    }

    public int getUserCount()
    {
        return gameEnvironment.getHabboManager().getOnlineCount();
    }

    public int getRoomCount()
    {
        return gameEnvironment.getRoomManager().getActiveRooms().size();
    }

    private static final String logo =
            "                    _                          ______                 _       _             _ \n" +
            "     /\\            | |                        |  ____|               | |     | |           | |\n" +
            "    /  \\   _ __ ___| |_ _   _ _ __ _   _ ___  | |__   _ __ ___  _   _| | __ _| |_ ___  _ __| |\n" +
            "   / /\\ \\ | '__/ __| __| | | | '__| | | / __| |  __| | '_ ` _ \\| | | | |/ _` | __/ _ \\| '__| |\n" +
            "  / ____ \\| | | (__| |_| |_| | |  | |_| \\__ \\ | |____| | | | | | |_| | | (_| | || (_) | |  |_|\n" +
            " /_/    \\_\\_|  \\___|\\__|\\__,_|_|   \\__,_|___/ |______|_| |_| |_|\\__,_|_|\\__,_|\\__\\___/|_|  (_)\n" +
            "                                                                                              \n" +
            "                                                                                              ";
}