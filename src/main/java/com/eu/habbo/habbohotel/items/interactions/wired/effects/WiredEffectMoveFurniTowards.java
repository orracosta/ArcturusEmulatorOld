package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
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
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveFurniTowards extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.CHASE;

    private THashSet<HabboItem> items;

    public WiredEffectMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        items = new THashSet<HabboItem>();
    }

    public WiredEffectMoveFurniTowards(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        items = new THashSet<HabboItem>();
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
            RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
            double shortest = 1000.0D;

            Habbo target = null;

            for(Habbo habbo : room.getCurrentHabbos().valueCollection())
            {
                RoomTile h = habbo.getRoomUnit().getCurrentLocation();

                double distance = t.distance(h);
                if(distance <= shortest)
                {
                    target = habbo;
                    shortest = distance;
                }
            }

            if(target != null)
            {
                if(PathFinder.tilesAdjecent(target.getRoomUnit().getX(), target.getRoomUnit().getY(), item.getX(), item.getY()) && (target.getRoomUnit().getX() == item.getX() || target.getRoomUnit().getY() == item.getY()))
                {
                    WiredHandler.handle(WiredTriggerType.COLLISION, target.getRoomUnit(), room, new Object[]{item});
                    continue;
                }

                int x = 0;
                int y = 0;

                if(target.getRoomUnit().getX() == item.getX())
                {
                    if (item.getY() < target.getRoomUnit().getY())
                        y++;
                    else
                        y--;
                }
                else if(target.getRoomUnit().getY() == item.getY())
                {
                    if (item.getX() < target.getRoomUnit().getX())
                        x++;
                    else
                        x--;
                }
                else if (target.getRoomUnit().getX() - item.getX() > target.getRoomUnit().getY() - item.getY())
                {
                    if (target.getRoomUnit().getX() - item.getX() > 0 )
                        x++;
                    else
                        x--;
                }
                else
                {
                    if (target.getRoomUnit().getY() - item.getY() > 0)
                        y++;
                    else
                        y--;
                }

                RoomTile newTile = room.getLayout().getTile((short) (item.getX() + x), (short) (item.getY() + y));

                if (newTile != null && newTile.state == RoomTileState.OPEN)
                {
                    if (room.getLayout().tileExists(newTile.x, newTile.y))
                    {
                        HabboItem topItem = room.getTopItemAt(newTile.x, newTile.y);

                        if (topItem == null || topItem.getBaseItem().allowStack())
                        {
                            double offsetZ = 0;

                            if (topItem != null)
                                offsetZ = topItem.getZ() + topItem.getBaseItem().getHeight() - item.getZ();

                            room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, offsetZ, room).compose());
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
        String wiredData = getDelay() + "\t";

        if(items != null && !items.isEmpty())
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
