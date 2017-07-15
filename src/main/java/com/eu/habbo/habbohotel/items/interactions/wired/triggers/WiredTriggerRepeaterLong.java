package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.WiredRepeatTask;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredTriggerRepeaterLong extends InteractionWiredTrigger
{
    private static final WiredTriggerType type = WiredTriggerType.PERIODICALLY_LONG;
    public static final int DEFAULT_DELAY = 20 * 5000;
    private int repeatTime = DEFAULT_DELAY;
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
        this.repeatTime = DEFAULT_DELAY;
        this.task = null;
    }

    @Override
    public WiredTriggerType getType()
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
        message.appendInt(1);
        message.appendInt(this.repeatTime / 5000);
        message.appendInt(0);
        message.appendInt(this.getType().code);

        if (!this.isTriggeredByRoomUnit())
        {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getEffects(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredEffect>()
            {
                @Override
                public boolean execute(InteractionWiredEffect object)
                {
                    if (object.requiresTriggeringUser())
                    {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers)
            {
                message.appendInt(i);
            }
        }
        else
        {
            message.appendInt(0);
        }
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
    public void onPlace(Room room)
    {
        if(this.task == null)
        {
            this.task = new WiredRepeatTask(this, room);
            Emulator.getThreading().run(this.task, this.repeatTime);
        }
    }
}
