package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
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
 * Created on 13-12-2014 20:51.
 */
public class WiredConditionLessTimeElapsed extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.TIME_LESS_THAN;

    private int cycles;

    public WiredConditionLessTimeElapsed(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionLessTimeElapsed(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        return (Emulator.getIntUnixTimestamp() - room.getLastTimerReset()) / 0.5 < this.cycles;
    }

    @Override
    public String getWiredData()
    {
        return this.cycles + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String data = set.getString("wired_data");

        try
        {
            if (!data.equals(""))
                this.cycles = Integer.valueOf(data);
        }
        catch (Exception e)
        {
            return;
        }
    }

    @Override
    public void onPickUp()
    {
        this.cycles = 0;
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
        message.appendInt32(1);
        message.appendInt32(this.cycles);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.cycles = packet.readInt();

        return true;
    }
}
