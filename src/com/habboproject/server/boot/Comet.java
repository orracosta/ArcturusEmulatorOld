package com.habboproject.server.boot;

import com.habboproject.server.boot.utils.ConsoleCommands;
import com.habboproject.server.boot.utils.ShutdownHook;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class Comet {
    /**
     * Logging during start-up & console commands
     */
    private static Logger log = Logger.getLogger(Comet.class.getName());

    /**
     * The main server instance
     */
    private static CometServer server;

    /**
     * The time the server was started
     */
    public static long start;

    /**
     * Is a debugger attached?
     */
    public static volatile boolean isDebugging = false;

    /**
     * Is Comet running?
     */
    public static volatile boolean isRunning = true;

    /**
     * Whether or not we want to show the GUI
     */
    public static boolean showGui = false;

    /**
     * Start the server!
     *
     * @param args The arguments passed from the execute command
     */
    public static void main(String[] args) {
        start = System.currentTimeMillis();

        try {
            PropertyConfigurator.configure(new FileInputStream("./config/log4j.properties"));
        } catch (Exception e) {
            log.error("Error while loading log4j configuration", e);
            return;
        }

        log.info("Comet Server - " + getBuild());

        log.info(Comet.logo);

        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.contains("dt_")) {
                isDebugging = true;
                break;
            }
        }

        Level logLevel = Level.INFO;

        if (args.length < 1) {
            log.debug("No config args found, falling back to default configuration!");
            server = new CometServer(null);
        } else {
            Map<String, String> cometConfiguration = new HashMap<>();

            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--debug-logging")) {
                    logLevel = Level.DEBUG;
                }

                if(args[i].equals("--gui")) {
                    // start GUI!
                    showGui = true;
                }

                if (!args[i].contains("="))
                    continue;

                String[] splitArgs = args[i].split("=");

                cometConfiguration.put(splitArgs[0], splitArgs.length != 1 ? splitArgs[1] : "");
            }

            server = new CometServer(cometConfiguration);
        }

        Logger.getRootLogger().setLevel(logLevel);
        server.init();

        ConsoleCommands.init();
        ShutdownHook.init();
    }

    /**
     * Exit the comet server
     *
     * @param message The message to display to the console
     */
    public static void exit(String message) {
        log.error("Comet has shutdown. Reason: \"" + message + "\"");
        System.exit(0);
    }

    /**
     * Get the current time in seconds
     *
     * @return The time in seconds
     */
    public static long getTime() {
        return (System.currentTimeMillis() / 1000L);
    }

    /**
     * Get the current build of Comet
     *
     * @return The current build of Comet
     */
    public static String getBuild() {
        return Comet.class.getPackage().getImplementationVersion() == null ? "3.0.0" : Comet.class.getPackage().getImplementationVersion();
    }

    /**
     * Get the main server instance
     *
     * @return The main server instance
     */
    public static CometServer getServer() {
        return server;
    }

    public static String logo = "\n\n" +
            "  /$$$$$$                                                  \n" +
            " /$$__  $$                                                 \n" +
            "| $$  \\ $$ /$$   /$$  /$$$$$$   /$$$$$$   /$$$$$$  /$$$$$$ \n" +
            "| $$$$$$$$| $$  | $$ /$$__  $$ /$$__  $$ /$$__  $$|____  $$\n" +
            "| $$__  $$| $$  | $$| $$  \\__/| $$  \\ $$| $$  \\__/ /$$$$$$$\n" +
            "| $$  | $$| $$  | $$| $$      | $$  | $$| $$      /$$__  $$\n" +
            "| $$  | $$|  $$$$$$/| $$      |  $$$$$$/| $$     |  $$$$$$$\n" +
            "|__/  |__/ \\______/ |__/       \\______/ |__/      \\_______/\n" +
            "                                                           \n";
}