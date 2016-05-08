package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredTriggerReset;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.WiredExecuteTask;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 19:59.
 */
public class WiredTriggerAtSetTime extends InteractionWiredTrigger implements WiredTriggerReset
{
    public final static WiredTriggerType type = WiredTriggerType.AT_GIVEN_TIME;

    public int executeTime;
    public int taskId;

    public WiredTriggerAtSetTime(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerAtSetTime(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        return this.executeTime + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        if(set.getString("wired_data").length() >= 1)
        {
            this.executeTime = (Integer.valueOf(set.getString("wired_data")));
        }

        if(this.executeTime < 500)
        {
            this.executeTime = 20 * 500;
        }
        this.taskId = 1;
        Emulator.getThreading().run(new WiredExecuteTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId())), executeTime);
    }

    @Override
    public void onPickUp()
    {
        this.executeTime = 0;
        this.taskId = 0;
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
        message.appendInt32(1);
        message.appendInt32(this.executeTime / 500);
        message.appendInt32(1);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        this.executeTime = packet.readInt() * 500;

        this.resetTimer();

        return true;
    }

    @Override
    public void resetTimer()
    {
        this.taskId++;

        Emulator.getThreading().run(new WiredExecuteTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId())), executeTime);
    }
}
