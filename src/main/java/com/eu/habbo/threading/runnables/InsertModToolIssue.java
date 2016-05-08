package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertModToolIssue implements Runnable
{
    private final ModToolIssue issue;

    public InsertModToolIssue(ModToolIssue issue)
    {
        this.issue = issue;
    }

    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO support_tickets (state, timestamp, score, sender_id, reported_id, room_id, mod_id, issue) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setInt(1, this.issue.state.getState());
            statement.setInt(2, this.issue.timestamp);
            statement.setInt(3, this.issue.priority);
            statement.setInt(4, this.issue.senderId);
            statement.setInt(5, this.issue.reportedId);
            statement.setInt(6, this.issue.roomId);
            statement.setInt(7, this.issue.modId);
            statement.setString(8, this.issue.message);
            statement.execute();

            ResultSet key = statement.getGeneratedKeys();
            key.first();

            this.issue.id = key.getInt(1);

            key.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
