package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTileEffectRemove extends InteractionDefault
{
    public InteractionTileEffectRemove(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTileEffectRemove(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, final Room room, Object[] objects) throws Exception
    {
        if(roomUnit.getEffectId() != 0)
        {
            roomUnit.setEffectId(0);
            room.sendComposer(new RoomUserEffectComposer(roomUnit).compose());
        }
    }
}
