package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 20-6-2015 11:50.
 */
public class WiredEffectBotWalkToFurni extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.BOT_MOVE;

    private THashSet<HabboItem> items;
    private String botName = "";

    public WiredEffectBotWalkToFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredEffectBotWalkToFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt32(item.getId());

        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.botName);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        this.botName = packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
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
        if(this.items.isEmpty())
            return false;

        List<Bot> bots = room.getBots(this.botName);

        if(bots.isEmpty())
            return false;

        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        for(Bot bot : bots)
        {
            int i = Emulator.getRandom().nextInt(this.items.size()) + 1;
            int j = 1;
            for (HabboItem item : this.items)
            {
                if(item.getRoomId() != 0 && item.getRoomId() == bot.getRoom().getId())
                {
                    if (i == j)
                    {
                        bot.getRoomUnit().setGoalLocation(item.getX(), item.getY());
                        break;
                    } else
                    {
                        j++;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected String getWiredData()
    {
        String wiredData = this.botName + "" + ((char) 9);

        if(items != null && !items.isEmpty())
        {
            for (HabboItem item : this.items)
            {
                if(item.getRoomId() != 0)
                {
                    wiredData += item.getId() + "" + ((char) 9);
                }
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<HabboItem>();
        String wiredData = set.getString("wired_data");
        String[] data = wiredData.split(((char) 9) + "");

        if(data.length > 1)
        {
            this.botName = data[0];

            for(int i = 1; i < data.length; i++)
            {
                HabboItem item = room.getHabboItem(Integer.valueOf(data[i]));

                if (item != null)
                    this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
        this.botName = "";
    }
}
