package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectLeaveTeam extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.LEAVE_TEAM;

    public WiredEffectLeaveTeam(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectLeaveTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            if(habbo.getHabboInfo().getCurrentGame() != null)
            {
                room.getGame(habbo.getHabboInfo().getCurrentGame()).removeHabbo(habbo);
            }
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        return getDelay() + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.setDelay(Integer.valueOf(set.getString("wired_data")));
    }

    @Override
    public void onPickUp()
    {
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
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
        message.appendInt32(this.getDelay());

        if (this.requiresTriggeringUser())
        {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>()
            {
                @Override
                public boolean execute(InteractionWiredTrigger object)
                {
                    if (!object.isTriggeredByRoomUnit())
                    {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt32(invalidTriggers.size());
            for (Integer i : invalidTriggers)
            {
                message.appendInt32(i);
            }
        }
        else
        {
            message.appendInt32(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        packet.readString();
        packet.readInt();
        this.setDelay(packet.readInt());
        return true;
    }
}
