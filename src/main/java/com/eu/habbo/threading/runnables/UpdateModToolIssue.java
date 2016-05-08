package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 5-3-2015 10:13.
 */
public class UpdateModToolIssue implements Runnable
{
    private final ModToolIssue issue;

    public UpdateModToolIssue(ModToolIssue issue)
    {
        this.issue = issue;
    }

    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE support_tickets SET state = ?, type = ?, mod_id = ? WHERE id = ?");
            statement.setInt(1, this.issue.state.getState());
            statement.setInt(2, this.issue.type.getType());
            statement.setInt(3, this.issue.modId);
            statement.setInt(4, this.issue.id);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
