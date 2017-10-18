package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.util.callback.HTTPPostError;
import gnu.trove.set.hash.THashSet;
import sun.rmi.runtime.Log;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logging
{
    /**
     * File that packetlogs will be sved to.
     */
    private static File packets;

    /**
     * File that undefined packetlogs will be saved to.
     */
    private static File packetsUndefined;

    /**
     * File that packets that were improperly handled will be saved to.
     */
    private static File errorsPackets;

    /**
     * File that SQL errors will be saved to.
     */
    private static File errorsSQL;

    /**
     * File that runtime errors will be saved to.
     */
    private static File errorsRuntime;

    /**
     * File that debug logs will be saved to.
     */
    private static File debugFile;

    private static PrintWriter packetsWriter;
    private static PrintWriter packetsUndefinedWriter;
    private static PrintWriter errorsPacketsWriter;
    private static PrintWriter errorsSQLWriter;
    private static PrintWriter errorsRuntimeWriter;
    private static PrintWriter debugFileWriter;

    /**
     * Bright text.
     */
    public static final String ANSI_BRIGHT = "\u001B[1m";

    /**
     * Italicized text.
     */
    public static final String ANSI_ITALICS = "\u001B[3m";

    /**
     * Underlined text.
     */
    public static final String ANSI_UNDERLINE = "\u001B[4m";

    /**
     * Resets all text effects to normal console font.
     */
    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Error logging cache layer.
     * Used for bulk inserting into the database.
     */
    private final THashSet<Loggable> errorLogs = new THashSet<Loggable>();

    /**
     * Command log cache layer.
     * Used for bulk inserting into the database.
     */
    private final THashSet<Loggable> commandLogs = new THashSet<Loggable>();

    public Logging()
    {
        packets          = new File("logging//packets//defined.txt");
        packetsUndefined = new File("logging//packets//packets.txt");
        errorsPackets    = new File("logging//errors//packets.txt");
        errorsSQL        = new File("logging//errors//sql.txt");
        errorsRuntime    = new File("logging//errors//runtime.txt");
        debugFile        = new File("logging//debug.txt");

        try
        {
            if (!packets.exists())
            {
                if (!packets.getParentFile().exists())
                {
                    packets.getParentFile().mkdirs();
                }

                packets.createNewFile();
            }

            if (!packetsUndefined.exists())
            {
                if (!packetsUndefined.getParentFile().exists())
                {
                    packetsUndefined.getParentFile().mkdirs();
                }

                packetsUndefined.createNewFile();
            }

            if (!errorsPackets.exists())
            {
                if (!errorsPackets.getParentFile().exists())
                {
                    errorsPackets.getParentFile().mkdirs();
                }

                errorsPackets.createNewFile();
            }

            if (!errorsSQL.exists())
            {
                if (!errorsSQL.getParentFile().exists())
                {
                    errorsSQL.getParentFile().mkdirs();
                }

                errorsSQL.createNewFile();
            }

            if (!errorsRuntime.exists())
            {
                if (!errorsRuntime.getParentFile().exists())
                {
                    errorsRuntime.getParentFile().mkdirs();
                }

                errorsRuntime.createNewFile();
            }

            if (!debugFile.exists())
            {
                if (!debugFile.getParentFile().exists())
                {
                    debugFile.getParentFile().mkdirs();
                }

                debugFile.createNewFile();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            packetsWriter          = new PrintWriter(new FileWriter(packets, true));
            packetsUndefinedWriter = new PrintWriter(new FileWriter(packetsUndefined, true));
            errorsPacketsWriter    = new PrintWriter(new FileWriter(errorsPackets, true));
            errorsSQLWriter        = new PrintWriter(new FileWriter(errorsSQL, true));
            errorsRuntimeWriter    = new PrintWriter(new FileWriter(errorsRuntime, true));
            debugFileWriter        = new PrintWriter(new FileWriter(debugFile, true));
        }
        catch (IOException e)
        {
			System.out.println("[CRITICAL] FAILED TO LOAD LOGGING COMPONENT!");
        }
    }

    /**
     * Prints a starting message to the console.
     * @param line The message to print.
     */
    public void logStart(Object line)
    {
        System.out.println("[" + Logging.ANSI_BRIGHT + Logging.ANSI_GREEN + "LOADING" + Logging.ANSI_RESET + "] " + line.toString());
    }

    /**
     * Prints a shutdown message to the console.
     * @param line The message to print.
     */
    public void logShutdownLine(Object line)
    {
        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFileWriter, line.toString());
        }
        System.out.println("[" + Logging.ANSI_BRIGHT + Logging.ANSI_GREEN + "SHUTDOWN" + Logging.ANSI_RESET + "] " + line.toString());
    }

    public void logUserLine(Object line)
    {
        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFileWriter, line.toString());
        }

        if (Emulator.getConfig().getBoolean("debug.show.users"))
        {
            System.out.println("[USER] " + line.toString());
        }
    }
    
    public synchronized void logDebugLine(Object line)
    {
        if (line instanceof Throwable)
        {
            logErrorLine(line);
            return;
        }
        if (Emulator.getConfig().getBoolean("debug.mode")) {
            System.out.println("[DEBUG] " + line.toString());
        }

        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFileWriter, line.toString());
        }
    }
    
    public synchronized void logPacketLine(Object line)
    {
        if (Emulator.getConfig().getBoolean("debug.show.packets")) {
            System.out.println("[" + Logging.ANSI_BLUE + "PACKET" + Logging.ANSI_RESET + "]" + line.toString());
        }

        if(Emulator.getConfig().getBoolean("logging.packets"))
        {
            write(packetsWriter, line.toString());
        }
    }
    
    public synchronized void logUndefinedPacketLine(Object line)
    {
        if (Emulator.getConfig().getBoolean("debug.show.packets.undefined"))
        {
            System.out.println("[PACKET] [UNDEFINED] " + line.toString());
        }

        if (Emulator.getConfig().getBoolean("logging.packets.undefined"))
        {
            write(packetsUndefinedWriter, line.toString());
        }
    }
    
    public synchronized void logErrorLine(Object line)
    {
        if (Emulator.isReady && Emulator.getConfig().getBoolean("debug.show.errors"))
        {
            System.err.println("[ERROR] " + line.toString());
        }

        if (Emulator.getConfig().loaded && Emulator.getConfig().getBoolean("logging.errors.runtime"))
        {
            write(errorsRuntimeWriter, line);
        }

        if(line instanceof Throwable)
        {
            ((Throwable) line).printStackTrace();
            if (line instanceof SQLException)
            {
                this.logSQLException((SQLException) line);
                return;
            }
            Emulator.getThreading().run(new HTTPPostError((Throwable) line));

            this.errorLogs.add(new ErrorLog("Exception", (Throwable) line));

            return;
        }

        this.errorLogs.add(new ErrorLog("Emulator", line.toString()));
    }

    public void logSQLException(SQLException e)
    {
        if(Emulator.getConfig().getBoolean("logging.errors.sql"))
        {
            e.printStackTrace();
            write(errorsSQLWriter, e);

            Emulator.getThreading().run(new HTTPPostError(e));
        }
    }

    public void logPacketError(Object e)
    {
        if(Emulator.getConfig().getBoolean("logging.errors.packets"))
        {
            if(e instanceof Throwable)
                ((Exception) e).printStackTrace();

            write(errorsPacketsWriter, e);
        }

        if(e instanceof Throwable)
        {
            ((Throwable) e).printStackTrace();
            if (e instanceof SQLException)
            {
                this.logSQLException((SQLException) e);
                return;
            }

            Emulator.getThreading().run(new HTTPPostError((Throwable) e));
        }
    }
    
    public void handleException(Exception e)
    {
        e.printStackTrace();
    }

    private synchronized void write(PrintWriter printWriter, Object message)
    {
        if(printWriter != null && message != null)
        {
            if(message instanceof Throwable)
            {
                ((Exception) message).printStackTrace(printWriter);
            }
            else
            {
                printWriter.write("MSG: " + message.toString() + "\r\n");
            }

            printWriter.flush();
        }
    }

    public void addLog(Loggable log)
    {
        if (log instanceof ErrorLog)
        {
            synchronized (this.errorLogs)
            {
                this.errorLogs.add(log);
            }
        }
        else if (log instanceof CommandLog)
        {
            synchronized (this.commandLogs)
            {
                this.commandLogs.add(log);
            }
        }
    }

    public void saveLogs()
    {
        if (Emulator.getDatabase() != null && Emulator.getDatabase().getDataSource() != null)
        {
            if (!this.errorLogs.isEmpty() || !this.commandLogs.isEmpty())
            {
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                {
                    if (!this.errorLogs.isEmpty())
                    {
                        synchronized (this.errorLogs)
                        {
                            try (PreparedStatement statement = connection.prepareStatement(ErrorLog.insertQuery))
                            {
                                for (Loggable log : this.errorLogs)
                                {
                                    log.log(statement);
                                }
                                statement.executeBatch();
                            }
                            this.errorLogs.clear();
                        }
                    }

                    if (!this.commandLogs.isEmpty())
                    {
                        synchronized (this.commandLogs)
                        {
                            try (PreparedStatement statement = connection.prepareStatement(CommandLog.insertQuery))
                            {
                                for (Loggable log : this.commandLogs)
                                {
                                    log.log(statement);
                                }

                                statement.executeBatch();
                            }
                            this.commandLogs.clear();
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }
    }

    public static PrintWriter getPacketsWriter()
    {
        return packetsWriter;
    }

    public static PrintWriter getPacketsUndefinedWriter()
    {
        return packetsUndefinedWriter;
    }

    public static PrintWriter getErrorsPacketsWriter()
    {
        return errorsPacketsWriter;
    }

    public static PrintWriter getErrorsSQLWriter()
    {
        return errorsSQLWriter;
    }

    public static PrintWriter getErrorsRuntimeWriter()
    {
        return errorsRuntimeWriter;
    }

    public static PrintWriter getDebugFileWriter()
    {
        return debugFileWriter;
    }
}