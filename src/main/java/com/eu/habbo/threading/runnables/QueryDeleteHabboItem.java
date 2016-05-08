package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 22-10-2014 11:23.
 */
public class QueryDeleteHabboItem implements Runnable
{
    private final HabboItem item;

    public QueryDeleteHabboItem(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM items WHERE id = ?");
            statement.setInt(1, item.getId());
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
