package com.eu.habbo.core;

import com.eu.habbo.Emulator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 12-11-2015 19:16.
 */
public class ErrorLog implements Loggable
{
    public final static String insertQuery = "INSERT INTO emulator_errors (timestamp, type, stacktrace) VALUES (?, ?, ?)";
    public final int timeStamp;
    public final String type;
    public final String stackTrace;

    public ErrorLog(String type, Throwable e)
    {
        this.timeStamp = Emulator.getIntUnixTimestamp();
        this.type = type;

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        this.stackTrace = sw.toString();

        try
        {
            pw.close();
            sw.close();
        } catch (IOException e1)
        {
            Emulator.getLogging().logErrorLine(e1);
        }
    }

    public ErrorLog(String type, String message)
    {
        this.timeStamp = Emulator.getIntUnixTimestamp();
        this.type = type;
        this.stackTrace = message;
    }

    @Override
    public void log(PreparedStatement statement) throws SQLException
    {
        statement.setInt(1, this.timeStamp);
        statement.setString(2, this.type);
        statement.setString(3, this.stackTrace);
        statement.addBatch();
    }
}
