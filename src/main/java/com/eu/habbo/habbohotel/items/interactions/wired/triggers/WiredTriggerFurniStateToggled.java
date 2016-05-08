package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 19:59.
 */
public class WiredTriggerFurniStateToggled extends InteractionWiredTrigger
{
    private static final WiredTriggerType type = WiredTriggerType.STATE_CHANGED;

    private THashSet<HabboItem> items;
    private String message = "";

    public WiredTriggerFurniStateToggled(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredTriggerFurniStateToggled(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(stuff.length >= 1)
        {
            Habbo habbo = room.getHabbo(roomUnit);

            if (habbo != null)
            {
                if (stuff[0] instanceof HabboItem)
                {
                    if(items.contains(stuff[0]))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = super.getDelay() + ":\t:";

        if(items != null)
        {
            if (!items.isEmpty())
            {
                for (HabboItem item : this.items)
                {
                    wiredData += item.getId() + ";";
                }
            } else
                wiredData += "\t";
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<HabboItem>();
        String wiredData = set.getString("wired_data");
        if(wiredData.split(":").length >= 3)
        {
            super.setDelay(Integer.valueOf(wiredData.split(":")[0]));
            this.message = wiredData.split(":")[1];

            if (!wiredData.split(":")[2].equals("\t"))
            {
                for (String s : wiredData.split(":")[2].split(";"))
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
        this.message = "";
    }

    @Override
    public WiredTriggerType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
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

        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(this.items.size());
        for(HabboItem item : this.items)
        {
            message.appendInt32(item.getId());
        }
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.message);
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
        packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

        return true;
    }
}
