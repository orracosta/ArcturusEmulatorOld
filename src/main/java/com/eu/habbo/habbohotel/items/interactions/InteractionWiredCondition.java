package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.IWired;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.wired.WiredConditionDataComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 19:53.
 */
public abstract class InteractionWiredCondition extends InteractionWired implements IWired
{
    public InteractionWiredCondition(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionWiredCondition(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(room.hasRights(client.getHabbo()))
            client.sendResponse(new WiredConditionDataComposer(this));
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    public abstract WiredConditionType getType();

    public abstract void serializeWiredData(ServerMessage message);

    public abstract boolean saveData(ClientMessage packet);

}
