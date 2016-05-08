package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:18.
 */
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
        return "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {

    }

    @Override
    public void onPickUp()
    {

    }

    @Override
    public WiredEffectType getType()
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
