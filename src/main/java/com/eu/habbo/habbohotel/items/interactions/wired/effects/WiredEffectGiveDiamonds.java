package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
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

/**
 * Created on 20-10-2015 21:37.
 */
public class WiredEffectGiveDiamonds extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private int points = 0;

    public WiredEffectGiveDiamonds(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveDiamonds(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        message.appendString(this.points + "");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(type.code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        try
        {
            this.points = Integer.valueOf(packet.readString());
        }
        catch (Exception e)
        {
            return false;
        }

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

        habbo.getHabboInfo().addCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type"), this.points);
        habbo.getClient().sendResponse(new UserPointsComposer(habbo.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type")), this.points, Emulator.getConfig().getInt("seasonal.primary.type")));

        return true;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "\t" + this.points;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String wireData = set.getString("wired_data");
        String[] data = wireData.split("\t");
        this.points = 0;

        if(data.length >= 2)
        {
            super.setDelay(Integer.valueOf(data[0]));

            try
            {
                this.points = Integer.valueOf(data[1]);
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
        this.points = 0;
    }
}
