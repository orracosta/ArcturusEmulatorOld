package com.eu.habbo;

import com.eu.habbo.core.CleanerThread;
import com.eu.habbo.core.ConfigurationManager;
import com.eu.habbo.core.Logging;
import com.eu.habbo.core.TextsManager;
import com.eu.habbo.database.Database;
import com.eu.habbo.habbohotel.GameEnvironment;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.gameserver.GameServer;
import com.eu.habbo.networking.rconserver.RCONServer;
import com.eu.habbo.plugin.PluginManager;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStartShutdownEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStoppedEvent;
import com.eu.habbo.threading.ThreadPooling;
import com.eu.habbo.threading.runnables.CameraClientAutoReconnect;
import com.eu.habbo.util.imager.badges.BadgeImager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public final class Emulator
{
    public static MessengerBuddy publicChatBuddy;
    public static final String version = "Version: 1.0.8";
    public static boolean isReady = false;
    public static boolean isShuttingDown = false;
    public static boolean stopped = false;
    public static boolean debugging = true;

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
            public void run()
            {
                Emulator.dispose();
            }
        });
        hook.setPriority(10);
        Runtime.getRuntime().addShutdownHook(hook);
    }
    
    public static void main(String[] args) throws Exception
    {
        try
        {
            Emulator.stopped = false;
            Emulator.logging = new Logging();
            Emulator.getLogging().logStart("\r" + Emulator.logo);
            random = new Random();
            publicChatBuddy = new MessengerBuddy(0, "Staff Chat", "", (short) 0, 0);
            long startTime = System.nanoTime();

            Emulator.runtime = Runtime.getRuntime();
            Emulator.threading = new ThreadPooling((Runtime.getRuntime().availableProcessors() * 2) + 100);
            Emulator.config = new ConfigurationManager("config.ini");
            Emulator.database = new Database(Emulator.getConfig());
            Emulator.config.loaded = true;
            Emulator.config.loadFromDatabase();
            Emulator.pluginManager = new PluginManager();
            Emulator.pluginManager.reload();
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void dispose()
    {
        Emulator.isShuttingDown = true;
        Emulator.isReady = false;
        Emulator.getLogging().logShutdownLine("Stopping Arcturus Emulator " + version + "...");

        if (Emulator.getPluginManager() != null)
            Emulator.getPluginManager().fireEvent(new EmulatorStartShutdownEvent());

        if (Emulator.cameraClient != null)
            Emulator.cameraClient.disconnect();

        if (Emulator.gameEnvironment != null)
            Emulator.gameEnvironment.dispose();

        if (Emulator.rconServer != null)
            Emulator.rconServer.stop();

        if (Emulator.gameServer != null)
            Emulator.gameServer.stop();

        if (Emulator.threading != null)
            Emulator.threading.shutDown();

        if (Emulator.getPluginManager() != null)
            Emulator.getPluginManager().fireEvent(new EmulatorStoppedEvent());

        if (Emulator.pluginManager != null)
            Emulator.pluginManager.dispose();


        Emulator.getLogging().logShutdownLine("Stopped Arcturus Emulator " + version + "...");
        Emulator.stopped = true;
    }
    
    public static ConfigurationManager getConfig()
    {
        return config;
    }

    public static TextsManager getTexts()
    {
        return texts;
    }
    
    public static Database getDatabase()
    {
        return database;
    }
    
    public static Runtime getRuntime()
    {
        return runtime;
    }
    
    public static GameServer getGameServer()
    {
        return gameServer;
    }

    public static RCONServer getRconServer()
    {
        return rconServer;
    }
    
    public static Logging getLogging()
    {
        return logging;
    }

    public static ThreadPooling getThreading() { return threading; }

    public static GameEnvironment getGameEnvironment() { return gameEnvironment; }

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
    
    private static String dateToUnixTimestamp(Date fecha)
    {
        String res = "";
        Date aux = stringToDate("1970-01-01 00:00:00");
        Timestamp aux1 = dateToTimeStamp(aux);
        Timestamp aux2 = dateToTimeStamp(fecha);
        long difference = aux2.getTime() - aux1.getTime();
        long seconds = difference / 1000L;
        return res + seconds;
    }
    
    private static Date stringToDate(String fecha)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date res = null;
        try
        {
            res = format.parse(fecha);
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
        return Integer.parseInt(getUnixTimestamp());
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

    public static boolean validString(String string)
    {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(string).find();
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