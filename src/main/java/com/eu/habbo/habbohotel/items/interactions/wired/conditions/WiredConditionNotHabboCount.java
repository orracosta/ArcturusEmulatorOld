package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:53.
 */
public class WiredConditionNotHabboCount extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.NOT_USER_COUNT;

    private int lowerLimit = 10;
    private int upperLimit = 20;

    public WiredConditionNotHabboCount(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionNotHabboCount(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        int count = room.getUserCount();

        return count < this.lowerLimit || count > this.upperLimit;
    }

    @Override
    public String getWiredData()
    {
        return this.lowerLimit + ":" + this.upperLimit;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(":");
        this.lowerLimit = Integer.valueOf(data[0]);
        this.upperLimit = Integer.valueOf(data[1]);
    }

    @Override
    public void onPickUp()
    {
        this.upperLimit = 0;
        this.lowerLimit = 0;
    }

    @Override
    public WiredConditionType getType()
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
        message.appendInt32(2);
            message.appendInt32(this.lowerLimit);
            message.appendInt32(this.upperLimit);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.lowerLimit = packet.readInt();
        this.upperLimit = packet.readInt();

        return true;
    }
}
