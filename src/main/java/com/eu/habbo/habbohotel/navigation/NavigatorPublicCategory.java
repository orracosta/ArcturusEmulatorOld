package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NavigatorPublicCategory
{
    public final int id;
    public final String name;
    public final List<Room> rooms;
    public final ListMode image;

    public NavigatorPublicCategory(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.image = set.getString("image").equals("1") ? ListMode.THUMBNAILS : ListMode.LIST;

        this.rooms = new ArrayList<Room>();
    }

    public void addRoom(Room room)
    {
        room.preventUncaching = true;
        this.rooms.add(room);
    }
}