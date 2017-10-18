package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRoomAds extends InteractionCustomValues
{
    public final static THashMap<String, String> defaultValues = new THashMap<String, String>()
    {
        {put("imageUrl", "http://arcturus.wf");}
        {put("clickUrl", "http://arcturus.wf");}
        {put("offsetX", "0");}
        {put("offsetY", "0");}
        {put("offsetZ", "0");}
    };

    public InteractionRoomAds(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, defaultValues);
    }

    public InteractionRoomAds(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, defaultValues);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }
}
