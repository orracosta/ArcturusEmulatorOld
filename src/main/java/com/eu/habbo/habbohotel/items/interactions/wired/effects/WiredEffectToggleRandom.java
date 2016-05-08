package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
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

public class WiredEffectToggleRandom extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.TOGGLE_RANDOM;

    private final THashSet<HabboItem> items =  new THashSet<HabboItem>();

    public WiredEffectToggleRandom(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectToggleRandom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
        {
            message.appendInt32(item.getId());
        }
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
        synchronized (this.items)
        {
            packet.readInt();
            packet.readString();

            this.items.clear();

            int count = packet.readInt();

            for (int i = 0; i < count; i++)
            {
                this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
            }
        }

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        //Habbo habbo = room.getHabbo(roomUnit);
        THashSet<HabboItem> items = this.items;

        for(HabboItem item : items)
        {
            if(item.getRoomId() == 0)
            {
                this.items.remove(item);
                continue;
            }

            try
            {
                item.setExtradata(Emulator.getRandom().nextInt(item.getBaseItem().getStateCount() + 1) + "");
                room.updateItem(item);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = "";

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
        String wiredData = set.getString("wired_data");

        if(wiredData.contains(";"))
        {
            for (String s : wiredData.split(";"))
            {
                HabboItem item = room.getHabboItem(Integer.valueOf(s));

                if(item != null)
                    this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }
}
