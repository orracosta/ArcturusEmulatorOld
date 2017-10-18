package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
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

public class WiredConditionNotFurniHaveHabbo extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.NOT_FURNI_HAVE_HABBO;

    private boolean all;
    private THashSet<HabboItem> items;

    public WiredConditionNotFurniHaveHabbo(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredConditionNotFurniHaveHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        this.refresh();

        if(this.items.isEmpty())
            return true;

        for(HabboItem item : this.items)
        {
            THashSet<Habbo> habbos = room.getHabbosOnItem(item);

            if(habbos.isEmpty())
                return true;
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        this.refresh();

        String data = (this.all ? "1" : "0") + ":";

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

        String[] data = set.getString("wired_data").split(":");

        if(data.length >= 1)
        {
            this.all = (data[0].equals("1"));

            if(data.length == 2)
            {
                String[] items = data[1].split(";");

                for (String s : items)
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if(item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
        this.all = false;
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
        message.appendInt(1);
        message.appendInt(this.all ? 1 : 0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        this.items.clear();

        int count;
        packet.readInt();

        packet.readString();

        count = packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room != null)
        {
            for (int i = 0; i < count; i++)
            {
                HabboItem item = room.getHabboItem(packet.readInt());

                if(item != null)
                    this.items.add(item);
            }

            return true;
        }

        return false;
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
                if (room.getHabboItem(item.getId()) == null)
                    items.add(item);
            }
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }
    }
}
