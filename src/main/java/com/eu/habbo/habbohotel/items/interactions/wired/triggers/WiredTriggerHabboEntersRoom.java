package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboEntersRoom extends InteractionWiredTrigger
{
    public static final WiredTriggerType type = WiredTriggerType.ENTER_ROOM;

    private String username = "";

    public WiredTriggerHabboEntersRoom(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerHabboEntersRoom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            if(username.length() > 0)
            {
                if(habbo.getHabboInfo().getUsername().equalsIgnoreCase(this.username))
                {
                    return true;
                }
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.username;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.username = set.getString("wired_data");
    }

    @Override
    public void onPickUp()
    {
        this.username = "";
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
        message.appendString(this.username);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        this.username = packet.readString();

        return true;
    }

    @Override
    public boolean isTriggeredByRoomUnit()
    {
        return true;
    }
}
