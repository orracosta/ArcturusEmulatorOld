package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.bots.Bot;
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
import java.util.List;

public class WiredEffectBotFollowHabbo extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.BOT_FOLLOW_AVATAR;

    private String botName = "";
    private int mode = 0;

    public WiredEffectBotFollowHabbo(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectBotFollowHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        message.appendString(this.botName);
        message.appendInt32(1);
        message.appendInt32(this.mode);
        message.appendInt32(1);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.mode = packet.readInt();
        this.botName = packet.readString();

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

        if(habbo != null)
        {
            List<Bot> bots = room.getBots(this.botName);
            for(Bot bot : bots)
            {
                if(this.mode == 1)
                    bot.startFollowingHabbo(habbo);
                else
                    bot.stopFollowingHabbo();
            }

            return true;
        }

        return false;
    }

    @Override
    protected String getWiredData()
    {
        return this.mode + ":" + this.botName;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(":");

        if(data.length == 2)
        {
            this.mode = (data[0].equalsIgnoreCase("1") ? 1 : 0);
            this.botName = data[1];
        }
    }

    @Override
    public void onPickUp()
    {
        this.botName = "";
        this.mode = 0;
    }
}
