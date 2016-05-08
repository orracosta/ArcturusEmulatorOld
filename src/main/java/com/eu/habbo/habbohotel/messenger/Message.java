package com.eu.habbo.habbohotel.messenger;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 26-8-2014 10:51.
 */
public class Message implements Runnable {

    private final int fromId;
    private final int toId;

    private final String message;
    private final int timestamp;

    public Message(int fromId, int toId, String message)
    {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;

        this.timestamp = Emulator.getIntUnixTimestamp();
    }

    @Override
    public void run() {
        if(Emulator.getConfig().getBoolean("save.private.chats", false))
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO chatlogs_private (user_from_id, user_to_id, message, timestamp) VALUES (?, ?, ?, ?)");
                statement.setInt(1, this.fromId);
                statement.setInt(2, this.toId);
                statement.setString(3, this.message);
                statement.setInt(4, this.timestamp);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public int getToId()
    {
        return this.toId;
    }

    public int getFromId() {
        return fromId;
    }

    public String getMessage() {
        return message;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
