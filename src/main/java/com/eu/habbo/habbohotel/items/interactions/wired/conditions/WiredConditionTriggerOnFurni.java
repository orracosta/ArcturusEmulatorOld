package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionTriggerOnFurni extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.TRIGGER_ON_FURNI;

    private THashSet<HabboItem> items = new THashSet<HabboItem>();

    public WiredConditionTriggerOnFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionTriggerOnFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        this.refresh();

        if(this.items.isEmpty())
            return true;

        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
            return true;

        for(HabboItem item : this.items)
        {
            if(RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()).contains(roomUnit.getX(), roomUnit.getY()))
                return true;
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        this.refresh();

        String data = "";

        for(HabboItem item : this.items)
        {
            data += item.getId() + ";";
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items.clear();

        String[] data = set.getString("wired_data").split(";");

        for(String s : data)
        {
            HabboItem item = room.getHabboItem(Integer.valueOf(s));

            if (item != null)
            {
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
    public WiredConditionType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());

        for(HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        this.items.clear();

        packet.readInt();
        packet.readString();

        int count = packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room != null)
        {
            for (int i = 0; i < count; i++)
            {
                HabboItem item = room.getHabboItem(packet.readInt());

                if (item != null)
                {
                    this.items.add(item);
                }
            }
        }

        return true;
    }

    private void refresh()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if(room == null)
        {
            items.addAll(this.items);
        }
        else
        {
            for (HabboItem item : this.items)
            {
                if (item.getRoomId() != room.getId())
                    items.add(item);
            }
        }

        this.items.removeAll(items);
    }
}
