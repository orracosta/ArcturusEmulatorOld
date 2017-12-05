package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredTriggerBotReachedFurni extends InteractionWiredTrigger
{
    public final static WiredTriggerType type = WiredTriggerType.BOT_REACHED_STF;

    private THashSet<HabboItem> items;
    private String botName = "";

    public WiredTriggerBotReachedFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredTriggerBotReachedFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public WiredTriggerType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        if(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()) == null)
        {
            items.addAll(this.items);
        }
        else
        {
            for (HabboItem item : this.items)
            {
                if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                    items.add(item);
            }
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for(HabboItem item : this.items)
        {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.botName);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);

        if (!this.isTriggeredByRoomUnit())
        {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getEffects(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredEffect>()
            {
                @Override
                public boolean execute(InteractionWiredEffect object)
                {
                    if (object.requiresTriggeringUser())
                    {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers)
            {
                message.appendInt(i);
            }
        }
        else
        {
            message.appendInt(0);
        }
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
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        List<Bot> bots = room.getBots(this.botName);

        for(Bot bot : bots)
        {
            if(bot.getRoomUnit().equals(roomUnit))
            {
                for(Object o : stuff)
                {
                    if(this.items.contains(o))
                        return true;
                }
            }
        }

        return false;
    }

    @Override
    protected String getWiredData()
    {
        String wiredData = this.botName + ":";

        if(!items.isEmpty())
        {
            for (HabboItem item : this.items)
            {
                wiredData += item.getId() + ";";
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items.clear();

        String[] data = set.getString("wired_data").split(":");

        if(data.length == 1)
        {
            this.botName = data[0];
        }
        else if(data.length == 2)
        {
            this.botName = data[0];

            String[] items = data[1].split(";");

            for(int i = 0; i < items.length; i++)
            {
                try
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(items[i]));

                    if (item != null)
                        this.items.add(item);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }
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
