package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 19:58.
 */
public class WiredTriggerGameStarts extends InteractionWiredTrigger
{
    private static final WiredTriggerType type = WiredTriggerType.GAME_STARTS;

    public WiredTriggerGameStarts(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerGameStarts(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        return true;
    }

    @Override
    public String getWiredData()
    {
        return "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        return;
    }

    @Override
    public void onPickUp()
    {

    }

    @Override
    public WiredTriggerType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        return true;
    }
}
