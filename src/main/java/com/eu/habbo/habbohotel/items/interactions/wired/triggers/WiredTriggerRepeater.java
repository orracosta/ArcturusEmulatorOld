package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredTriggerRepeater extends InteractionWiredTrigger implements ICycleable
{
    public static final WiredTriggerType type = WiredTriggerType.PERIODICALLY;
    public static final int DEFAULT_DELAY = 20 * 500;

    protected int repeatTime = DEFAULT_DELAY;
    protected int counter = 0;

    public WiredTriggerRepeater(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerRepeater(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        return this.repeatTime + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        if(set.getString("wired_data").length() >= 1)
        {
            this.repeatTime = (Integer.valueOf(set.getString("wired_data")));
        }

        if(this.repeatTime < 500)
        {
            this.repeatTime = 20 * 500;
        }
    }

    @Override
    public void onPickUp()
    {
        this.repeatTime = DEFAULT_DELAY;
        this.counter = 0;
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
        message.appendInt(this.repeatTime / 500);
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

        this.repeatTime = packet.readInt() * 500;
        this.counter = 0;

        if (this.repeatTime < 500)
        {
            this.repeatTime = 500;
        }

        return true;
    }

    @Override
    public void cycle(Room room)
    {
        this.counter += 500;
        if (this.counter >= this.repeatTime)
        {
            this.counter = 0;
            if (this.getRoomId() != 0)
            {
                if (room.isLoaded())
                {
                    WiredHandler.handle(this, null, room, new Object[]{this});
                }
            }
        }
    }
}
