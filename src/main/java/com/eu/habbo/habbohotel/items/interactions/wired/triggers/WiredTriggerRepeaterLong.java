package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.WiredRepeatTask;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:00.
 */
public class WiredTriggerRepeaterLong extends InteractionWiredTrigger
{
    private static final WiredTriggerType type = WiredTriggerType.PERIODICALLY_LONG;

    private int repeatTime = 20 * 5000;
    private WiredRepeatTask task;

    public WiredTriggerRepeaterLong(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        if(this.getRoomId() != 0)
        {
            this.task = new WiredRepeatTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
            Emulator.getThreading().run(this.task, this.repeatTime);
        }
    }

    public WiredTriggerRepeaterLong(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(this.getRoomId() != 0 && room.isLoaded())
        {
            Emulator.getThreading().run(this.task, this.repeatTime);

            if(room.isLoaded())
                return true;
        }
        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.repeatTime + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        if(set.getString("wired_data").length() >= 1)
        {
            this.repeatTime = (Integer.valueOf(set.getString("wired_data")));
        }

        if(this.repeatTime < 5000)
        {
            this.repeatTime = 20 * 5000;
        }

        if(this.getRoomId() != 0)
        {
            if(this.task == null)
            {
                this.task = new WiredRepeatTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
                Emulator.getThreading().run(this.task, this.repeatTime);
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.repeatTime = 20 * 5000;
        this.task = null;
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
        message.appendInt32(this.repeatTime / 5000);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.repeatTime = packet.readInt() * 5000;

        if(this.task == null)
        {
            this.task = new WiredRepeatTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
            Emulator.getThreading().run(this.task, this.repeatTime);
        }

        return true;
    }

    @Override
    public void onPlace()
    {
        if(this.task == null)
        {
            this.task = new WiredRepeatTask(this, Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
            Emulator.getThreading().run(this.task, this.repeatTime);
        }
    }
}
