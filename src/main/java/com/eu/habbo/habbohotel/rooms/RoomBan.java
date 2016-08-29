package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomBan
{
    public final int roomId;
    public final int userId;
    public final String username;
    public final int endTimestamp;

    public RoomBan(int roomId, int userId, String username, int endTimestamp)
    {
        this.roomId = roomId;
        this.userId = userId;
        this.username = username;
        this.endTimestamp = endTimestamp;
    }

    public RoomBan(ResultSet set) throws SQLException
    {
        this.roomId = set.getInt("room_id");
        this.userId = set.getInt("user_id");
        this.username = set.getString("username");
        this.endTimestamp = set.getInt("ends");
    }

    public void insert()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO room_bans (room_id, user_id, ends) VALUES (?, ?, ?)");
            statement.setInt(1, this.roomId);
            statement.setInt(2, this.userId);
            statement.setInt(3, this.endTimestamp);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void delete()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM room_bans WHERE room_id = ? AND user_id = ?");
            statement.setInt(1, this.roomId);
            statement.setInt(2, this.userId);
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
