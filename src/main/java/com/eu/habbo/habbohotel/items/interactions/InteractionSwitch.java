package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.PathFinder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionSwitch extends InteractionDefault
{
    public InteractionSwitch(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionSwitch(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canToggle(Habbo habbo, Room room)
    {
        return super.canToggle(habbo, room) || PathFinder.tilesAdjecent(this.getX(), this.getY(), habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());
    }
}
