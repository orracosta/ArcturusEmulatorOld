package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 30-1-2015 20:58.
 */
class QueryDeleteHabboBadge implements Runnable
{
    private final String name;
    private final Habbo habbo;

    public QueryDeleteHabboBadge(Habbo habbo, String name)
    {
        this.name = name;
        this.habbo = habbo;
    }
    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM user_badges WHERE users_id = ? AND badge_code = ?");
            statement.setInt(1, this.habbo.getHabboInfo().getId());
            statement.setString(2, this.name);
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
