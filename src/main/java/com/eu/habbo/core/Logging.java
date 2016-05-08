package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.util.callback.HTTPPostError;
import gnu.trove.set.hash.THashSet;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logging
{
    private static File packets;
    private static File packetsUndefined;
    private static File errorsPackets;
    private static File errorsSQL;
    private static File errorsRuntime;
    private static File debugFile;

    public static final String ANSI_BRIGHT = "\u001B[1m";
    public static final String ANSI_ITALICS = "\u001B[3m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final THashSet<ErrorLog> errorLogs = new THashSet<ErrorLog>();

    public Logging()
    {
        packets = new File("logging//packets//packets.txt");
        packetsUndefined = new File("logging//packets//undefined.txt");

        errorsPackets = new File("logging//errors/packets.txt");
        errorsSQL = new File("logging//errors/sql.txt");
        errorsRuntime = new File("logging//errors//runtime.txt");

        debugFile = new File("logging//debug.txt");

        try
        {
            if (!packets.exists())
                packets.createNewFile();

            if (!packetsUndefined.exists())
                packetsUndefined.createNewFile();

            if (!errorsPackets.exists())
                errorsPackets.createNewFile();

            if (!errorsSQL.exists())
                errorsSQL.createNewFile();

            if (!errorsRuntime.exists())
                errorsRuntime.createNewFile();

            if (!debugFile.exists())
                debugFile.createNewFile();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public void logStart(Object line)
    {
        System.out.println("[" + Logging.ANSI_BRIGHT + Logging.ANSI_GREEN + "LOADING" + Logging.ANSI_RESET + "] " + line.toString());
    }
    
    public void logShutdownLine(Object line)
    {
        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFile, line.toString());
        }
        System.out.println("[" + Logging.ANSI_BRIGHT + Logging.ANSI_GREEN + "SHUTDOWN" + Logging.ANSI_RESET + "] " + line.toString());
    }
    
    public void logUserLine(Object line)
    {
        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFile, line.toString());
        }
        System.out.println("[USER] " + line.toString());
    }
    
    public synchronized void logDebugLine(Object line)
    {
        if (Emulator.getConfig().getBoolean("debug.mode")) {
            System.out.println("[DEBUG] " + line.toString());
        }

        if(Emulator.getConfig().getBoolean("logging.debug"))
        {
            write(debugFile, line.toString());
        }
    }
    
    public synchronized void logPacketLine(Object line)
    {
        if (Emulator.getConfig().getBoolean("debug.show.packets")) {
            System.out.println("[" + Logging.ANSI_BLUE + "PACKET" + Logging.ANSI_RESET + "]" + line.toString());
        }

        if(Emulator.getConfig().getBoolean("logging.packets"))
        {
            write(packets, line.toString());
        }
    }
    
    public synchronized void logUndefinedPacketLine(Object line)
    {
        if (Emulator.getConfig().getBoolean("debug.show.packets.undefined")) {
            System.out.println("[PACKET] [UNDEFINED] " + line.toString());
        }

        if (Emulator.getConfig().getBoolean("logging.packets.undefined"))
        {
            write(packetsUndefined, line.toString());
        }
    }
    
    public synchronized void logErrorLine(Object line)
    {
        System.err.println("[ERROR] " + line.toString());

        if (Emulator.isReady && Emulator.getConfig().getBoolean("logging.errors.runtime"))
        {
            write(errorsRuntime, line);

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
    }

    public void logSQLException(SQLException e)
    {
        if(Emulator.getConfig().getBoolean("logging.errors.sql"))
        {
            e.printStackTrace();
            write(errorsSQL, e);

            Emulator.getThreading().run(new HTTPPostError(e));
        }
    }

    public void logPacketError(Object e)
    {
        if(Emulator.getConfig().getBoolean("logging.errors.packets"))
        {
            if(e instanceof Exception)
                ((Exception) e).printStackTrace();

            write(errorsPackets, e);
        }
    }
    
    public void handleException(Exception e)
    {
        e.printStackTrace();
    }

    private synchronized void write(File file, Object message)
    {
        if(file != null && message != null)
        {
            try
            {
                PrintWriter printWriter = new PrintWriter(new FileWriter(file, true));

                if(message instanceof Exception)
                {
                    ((Exception) message).printStackTrace(printWriter);
                }
                else
                {
                    printWriter.write(message.toString() + "\r\n");
                }
                printWriter.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveLogs()
    {
        synchronized (this.errorLogs)
        {
            PreparedStatement statement = Emulator.getDatabase().prepare(ErrorLog.insertQuery);

            for(ErrorLog log : this.errorLogs)
            {
                try
                {
                    log.log(statement);
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

            this.errorLogs.clear();

            try
            {
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}