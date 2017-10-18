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

public class WiredConditionHabboCount extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.USER_COUNT;

    private int lowerLimit = 0;
    private int upperLimit = 50;

    public WiredConditionHabboCount(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionHabboCount(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        int count = room.getUserCount();

        return count >= this.lowerLimit && count <= this.upperLimit;
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
        this.lowerLimit = 0;
        this.upperLimit = 50;
    }

    @Override
    public WiredConditionType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
            message.appendInt(this.lowerLimit);
            message.appendInt(this.upperLimit);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
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
