package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionMatchStatePosition extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.MATCH_SSHOT;

    private THashSet<WiredMatchFurniSetting> settings;

    private boolean state;
    private boolean position;
    private boolean direction;

    public WiredConditionMatchStatePosition(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.settings = new THashSet<WiredMatchFurniSetting>();
    }

    public WiredConditionMatchStatePosition(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.settings = new THashSet<WiredMatchFurniSetting>();
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
        message.appendInt(this.settings.size());

        for(WiredMatchFurniSetting item : this.settings)
            message.appendInt(item.itemId);

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(4);
            message.appendInt(this.state ? 1 : 0);
            message.appendInt(this.direction ? 1 : 0);
            message.appendInt(this.position ? 1 : 0);
            message.appendInt(10);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        this.settings.clear();

        int count;
        packet.readInt();

        this.state = packet.readInt() == 1;
        this.direction = packet.readInt() == 1;
        this.position = packet.readInt() == 1;

        packet.readString();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return true;

        count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            int itemId = packet.readInt();
            HabboItem item = room.getHabboItem(itemId);

            if (item != null)
                this.settings.add(new WiredMatchFurniSetting(item.getId(), item.getExtradata(), item.getRotation(), item.getX(), item.getY(), item.getZ()));
        }

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(this.settings.isEmpty())
            return true;

        THashSet<WiredMatchFurniSetting> s = new THashSet<WiredMatchFurniSetting>();

        for(WiredMatchFurniSetting setting : this.settings)
        {
            HabboItem item = room.getHabboItem(setting.itemId);

            if(item != null)
            {
                if(this.state)
                {
                    if(!item.getExtradata().equals(setting.state))
                        return false;
                }

                if(this.position)
                {
                    if(!(setting.x == item.getX() && setting.y == item.getY() && setting.z == item.getZ()))
                        return false;
                }

                if(this.direction)
                {
                    if(setting.rotation != item.getRotation())
                        return false;
                }
            }
            else
            {
                s.add(setting);
            }
        }

        if(!s.isEmpty())
        {
            for(WiredMatchFurniSetting setting : s)
            {
                this.settings.remove(setting);
            }
        }

        return true;
    }

    @Override
    public String getWiredData()
    {
        String data = this.settings.size() + ":";

        if(this.settings.isEmpty())
        {
            data += "\t;";
        }
        else
        {
            for (WiredMatchFurniSetting item : this.settings)
                data += item.toString() + ";";
        }

        data += ":" + (this.state ? 1 : 0) + ":" + (this.direction ? 1 : 0) + ":" + (this.position ? 1 : 0);

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(":");

        int itemCount = Integer.valueOf(data[0]);

        String[] items = data[1].split(";");

        for(int i = 0; i < itemCount; i++)
        {
            String[] stuff = items[i].split("-");

            if(stuff.length == 6)
                this.settings.add(new WiredMatchFurniSetting(Integer.valueOf(stuff[0]), stuff[1], Integer.valueOf(stuff[2]), Integer.valueOf(stuff[3]), Integer.valueOf(stuff[4]), Double.valueOf(stuff[5])));
        }

        this.state = data[2].equals("1");
        this.direction = data[3].equals("1");
        this.position = data[4].equals("1");
    }

    @Override
    public void onPickUp()
    {
        this.settings.clear();
        this.direction = false;
        this.position = false;
        this.state = false;
    }

    private void refresh()
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room != null)
        {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<WiredMatchFurniSetting>();

            for (WiredMatchFurniSetting setting : this.settings)
            {
                HabboItem item = room.getHabboItem(setting.itemId);
                if (item == null)
                {
                    remove.add(setting);
                }
            }

            for(WiredMatchFurniSetting setting : remove)
            {
                this.settings.remove(setting);
            }
        }
    }
}
