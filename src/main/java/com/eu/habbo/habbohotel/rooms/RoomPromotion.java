package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomPromotion
{
    private final Room room;
    private String title;
    private String description;
    private int endTimestamp;
    public boolean needsUpdate;

    public RoomPromotion(Room room, String title, String description, int endTimestamp)
    {
        this.room = room;
        this.title = title;
        this.description = description;
        this.endTimestamp = endTimestamp;
    }

    public RoomPromotion(Room room, ResultSet set) throws SQLException
    {
        this.room = room;
        this.title = set.getString("title");
        this.description = set.getString("description");
        this.endTimestamp = set.getInt("end_timestamp");
    }

    public void save()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE room_promotions SET title = ?, description = ? WHERE room_id = ?"))
            {
                statement.setString(1, this.title);
                statement.setString(2, this.description);
                statement.setInt(3, this.room.getId());
                statement.executeUpdate();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.needsUpdate = false;
        }
    }

    public Room getRoom()
    {
        return this.room;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getEndTimestamp()
    {
        return this.endTimestamp;
    }

    public void setEndTimestamp(int endTimestamp)
    {
        this.endTimestamp = endTimestamp;
    }

    public void addEndTimestamp(int time)
    {
        this.endTimestamp += time;
    }
}
