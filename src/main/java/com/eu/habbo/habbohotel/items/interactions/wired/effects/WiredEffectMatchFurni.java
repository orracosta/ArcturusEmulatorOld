package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMatchFurni extends InteractionWiredEffect
{
    private static final WiredEffectType type = WiredEffectType.MATCH_SSHOT;

    private THashSet<WiredMatchFurniSetting> settings;

    private boolean state = false;
    private boolean direction = false;
    private boolean position = false;

    public WiredEffectMatchFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.settings = new THashSet<WiredMatchFurniSetting>();
    }

    public WiredEffectMatchFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.settings = new THashSet<WiredMatchFurniSetting>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        //this.refresh();
        for(WiredMatchFurniSetting setting : this.settings)
        {
            HabboItem item = room.getHabboItem(setting.itemId);
            if(item != null)
            {
                if(this.state)
                {
                    if(!setting.state.equals("\t"))
                    {
                        item.setExtradata(setting.state);
                    }
                }

                if(this.direction)
                {
                    item.setRotation(setting.rotation);
                }

                room.updateItem(item);

                if(this.position)
                {
                    RoomTile t = room.getLayout().getTile((short)setting.x, (short)setting.y);

                    double offsetZ = setting.z - item.getZ();

                    room.sendComposer(new FloorItemOnRollerComposer(item, null, t, offsetZ, room).compose());
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData()
    {
        //this.refresh();

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

        data += ":" + (this.state ? 1 : 0) + ":" + (this.direction ? 1 : 0) + ":" + (this.position ? 1 : 0) + ":" + getDelay();

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
        this.setDelay(Integer.valueOf(data[5]));
    }

    @Override
    public void onPickUp()
    {
        this.settings.clear();
        this.state = false;
        this.direction = false;
        this.position = false;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        this.refresh();

        message.appendBoolean(false);
        message.appendInt32(Emulator.getConfig().getInt("hotel.wired.furni.selection.count"));
        message.appendInt32(this.settings.size());

        for(WiredMatchFurniSetting item : this.settings)
            message.appendInt32(item.itemId);

        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(4);
            message.appendInt32(this.state ? 1 : 0);
            message.appendInt32(this.direction ? 1 : 0);
            message.appendInt32(this.position ? 1 : 0);
            message.appendInt32(10);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(this.getDelay());
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        this.settings.clear();

        //packet.readInt();

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

        this.setDelay(packet.readInt());

        return true;
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
