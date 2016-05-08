package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.map.TIntObjectMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 7-8-2015 16:03.
 */
public class QueryDeleteHabboItems implements Runnable
{
    private TIntObjectMap<HabboItem> items;

    public QueryDeleteHabboItems(TIntObjectMap<HabboItem> items)
    {
        this.items = items;
    }

    @Override
    public void run()
    {
        PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM items WHERE id = ?");

        for(HabboItem item : items.valueCollection())
        {
            if(item.getRoomId() > 0)
                continue;

            try
            {
                statement.setInt(1, item.getId());
                statement.execute();
            } catch (Exception e){
                Emulator.getLogging().logErrorLine(e);
            }
        }

        try
        {
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.items.clear();
    }
}
