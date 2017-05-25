package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMuteHabbo extends InteractionWiredEffect
{
    private static final WiredEffectType type = WiredEffectType.MUTE_TRIGGER;

    public WiredEffectMuteHabbo(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectMuteHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
        message.appendInt32(0);
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

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(roomUnit == null)
            return true;

        roomUnit.wiredMuted = !roomUnit.wiredMuted;

        return true;
    }

    @Override
    public String getWiredData()
    {
        return getDelay() + "\t";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.setDelay(Integer.valueOf(set.getString("wired_data").split("\t")[0]));
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
    public boolean requiresTriggeringUser()
    {
        return true;
    }
}
