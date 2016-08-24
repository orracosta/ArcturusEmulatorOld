package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveFurniAway extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.FLEE;

    private THashSet<HabboItem> items = new THashSet<HabboItem>();

    public WiredEffectMoveFurniAway(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniAway(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
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

        for(HabboItem item : this.items)
        {
            Tile t = new Tile(item.getX(), item.getY(), 0);
            double shortest = 1000.0D;

            Habbo target = null;

            for(Habbo habbo : room.getCurrentHabbos().valueCollection())
            {
                Tile h = new Tile(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY(), 0);

                if(t.distance(h) <= shortest)
                {
                    shortest = t.distance(h);
                    target = habbo;
                }
            }

            if(target != null)
            {

                if(PathFinder.tilesAdjecent(target.getRoomUnit().getX(), target.getRoomUnit().getY(), item.getX(), item.getY()) && (target.getRoomUnit().getX() == item.getX() || target.getRoomUnit().getY() == item.getY()))
                {
                    WiredHandler.handle(WiredTriggerType.COLLISION, roomUnit, room, new Object[]{item});
                    continue;
                }

                int x = 0;
                int y = 0;
                boolean notFound = false;

                if(target.getRoomUnit().getX() == item.getX())
                {
                    if (item.getY() < target.getRoomUnit().getY())
                        y--;
                    else
                        y++;

                    Tile newTile = new Tile(item.getX() + x, item.getY() + y, 0);

                    if (room.getLayout().tileExists(newTile.X, newTile.Y))
                    {
                        HabboItem topItem = room.getTopItemAt(newTile.X, newTile.Y);

                        if (topItem == null || topItem.getBaseItem().allowStack())
                        {
                            if (topItem != null)
                                newTile.Z = topItem.getZ() + topItem.getBaseItem().getHeight();

                            room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, room).compose());
                            continue;
                        }
                    }
                    else
                    {
                        y = 0;
                        notFound = true;
                    }
                }

                if(target.getRoomUnit().getY() == item.getY() || notFound)
                {
                    if (item.getX() < target.getRoomUnit().getX())
                        x--;
                    else
                        x++;

                    Tile newTile = new Tile(item.getX() + x, item.getY() + y, 0);

                    if (room.getLayout().tileExists(newTile.X, newTile.Y))
                    {
                        HabboItem topItem = room.getTopItemAt(newTile.X, newTile.Y);

                        if (topItem == null || topItem.getBaseItem().allowStack())
                        {
                            if (topItem != null)
                                newTile.Z = topItem.getZ() + topItem.getBaseItem().getHeight();

                            room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, room).compose());
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = this.getDelay() + "\t";

        if(this.items != null && !this.items.isEmpty())
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
        this.items = new THashSet<HabboItem>();
        String[] wiredData = set.getString("wired_data").split("\t");

        if (wiredData.length >= 1)
        {
            this.setDelay(Integer.valueOf(wiredData[0]));
        }
        if (wiredData.length == 2)
        {
            if (wiredData[1].contains(";"))
            {
                for (String s : wiredData[1].split(";"))
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
        message.appendInt32(Emulator.getConfig().getInt("hotel.wired.furni.selection.count"));
        message.appendInt32(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt32(item.getId());

        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(this.getDelay());
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

        this.setDelay(packet.readInt());

        return true;
    }
}
