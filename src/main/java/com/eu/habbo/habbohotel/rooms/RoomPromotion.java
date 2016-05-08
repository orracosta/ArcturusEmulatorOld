package com.eu.habbo.habbohotel.rooms;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 15-11-2015 14:05.
 */
public class RoomPromotion
{
    private final Room room;
    private String title;
    private String description;
    private int endTimestamp;

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
