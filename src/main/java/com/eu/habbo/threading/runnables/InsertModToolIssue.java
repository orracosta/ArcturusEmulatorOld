package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;

import java.sql.*;

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
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO support_tickets (state, timestamp, score, sender_id, reported_id, room_id, mod_id, issue, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, this.issue.state.getState());
            statement.setInt(2, this.issue.timestamp);
            statement.setInt(3, this.issue.priority);
            statement.setInt(4, this.issue.senderId);
            statement.setInt(5, this.issue.reportedId);
            statement.setInt(6, this.issue.roomId);
            statement.setInt(7, this.issue.modId);
            statement.setString(8, this.issue.message);
            statement.setInt(9, this.issue.category);
            statement.execute();

            try (ResultSet key = statement.getGeneratedKeys())
            {
                if (key.first())
                {
                    this.issue.id = key.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
