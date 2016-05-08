package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.WiredResetTimers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:14.
 */
public class WiredEffectResetTimers extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.RESET_TIMERS;

    private int delay = 0;

    public WiredEffectResetTimers(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectResetTimers(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
        message.appendInt32(this.delay * 2);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        packet.readString();
        packet.readInt();
        this.delay = packet.readInt() / 2;

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Emulator.getThreading().run(new WiredResetTimers(room), this.delay);

        return true;
    }

    @Override
    public String getWiredData()
    {
        return this.delay + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String data = set.getString("wired_data");

        try
        {
            if (!data.equals(""))
                this.delay = Integer.valueOf(data);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void onPickUp()
    {
        this.delay = 0;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }
}
