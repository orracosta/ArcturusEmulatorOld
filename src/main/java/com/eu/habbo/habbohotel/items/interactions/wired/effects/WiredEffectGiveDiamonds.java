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

public class WiredEffectGiveDiamonds extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private int points = 0;
    private int pointsType = -1;

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
        message.appendString(this.points + (this.pointsType == -1 ? "" : ":" + this.pointsType));
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
            loadFromString(packet.readString());
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

        habbo.getHabboInfo().addCurrencyAmount(this.pointsType == -1 ? Emulator.getConfig().getInt("seasonal.primary.type") : this.pointsType, this.points);
        habbo.getClient().sendResponse(new UserPointsComposer(habbo.getHabboInfo().getCurrencyAmount(this.pointsType == -1 ? Emulator.getConfig().getInt("seasonal.primary.type") : this.pointsType), this.points, Emulator.getConfig().getInt("seasonal.primary.type")));

        return true;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "\t" + this.points + (this.pointsType == -1 ? "" : ":" + this.pointsType);
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
                loadFromString(data[1]);
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
        this.setDelay(0);
    }

    private void loadFromString(String data)
    {
        String[] pointsData = data.split(":");

        this.pointsType = -1;
        if (pointsData.length >= 1)
        {
            this.points = Integer.valueOf(pointsData[0]);
        }

        if (pointsData.length == 2)
        {
            this.pointsType = Integer.valueOf(pointsData[1]);
        }
    }
}
