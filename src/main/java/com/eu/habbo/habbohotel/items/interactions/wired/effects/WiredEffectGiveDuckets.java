package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveDuckets extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private int pixels = 0;

    public WiredEffectGiveDuckets(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveDuckets(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        message.appendBoolean(false);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.pixels + "");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(type.code);
        message.appendInt32(this.getDelay());
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        try
        {
            this.pixels = Integer.valueOf(packet.readString());
        }
        catch (Exception e)
        {
            return false;
        }

        packet.readInt();
        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
            return false;

        habbo.getHabboInfo().addCurrencyAmount(0, this.pixels);
        habbo.getClient().sendResponse(new UserPointsComposer(habbo.getHabboInfo().getCurrencyAmount(0), this.pixels, 0));

        return true;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "\t" + this.pixels;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String wireData = set.getString("wired_data");
        String[] data = wireData.split("\t");
        this.pixels = 0;

        if(data.length >= 2)
        {
            super.setDelay(Integer.valueOf(data[0]));

            try
            {
                this.pixels = Integer.valueOf(data[1]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.pixels = 0;
        this.setDelay(0);
    }
}

