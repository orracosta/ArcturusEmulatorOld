package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 23-7-2015 18:18.
 */
public class CustomRoomLayout extends RoomLayout implements Runnable
{
    private boolean needsUpdate;
    private Room room;

    public CustomRoomLayout(ResultSet set, Room room) throws SQLException
    {
        super(set);

        this.room = room;
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            this.needsUpdate = false;

            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE room_models_custom SET door_x = ?, door_y = ?, door_dir = ?, heightmap = ? WHERE id = ? LIMIT 1");

            try
            {
                statement.setInt(1, this.getDoorX());
                statement.setInt(2, this.getDoorY());
                statement.setInt(3, this.getDoorDirection());
                statement.setString(4, this.getHeightmap());
                statement.setInt(5, this.room.getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    public void needsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }
}
