package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveRotateFurni extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.MOVE_ROTATE;

    private int direction;
    private int rotation;
    private int delay;
    private THashSet<HabboItem> items = new THashSet<HabboItem>();

    public WiredEffectMoveRotateFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectMoveRotateFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
            if(this.direction > 0)
            {
                RoomUserRotation moveDirection = RoomUserRotation.NORTH;

                if(this.direction == 1)
                {
                    moveDirection = RoomUserRotation.values()[Emulator.getRandom().nextInt(RoomUserRotation.values().length / 2) * 2];
                }
                else if(this.direction == 2)
                {
                    if(Emulator.getRandom().nextInt(2) == 1)
                    {
                        moveDirection = RoomUserRotation.EAST;
                    }
                    else
                    {
                        moveDirection = RoomUserRotation.WEST;
                    }
                }
                else if(this.direction == 3)
                {
                    if(Emulator.getRandom().nextInt(2) == 1)
                    {
                        moveDirection = RoomUserRotation.NORTH;
                    }
                    else
                    {
                        moveDirection = RoomUserRotation.SOUTH;
                    }
                }
                else if(this.direction == 4)
                {
                    moveDirection = RoomUserRotation.NORTH;
                }
                else if(this.direction == 5)
                {
                    moveDirection = RoomUserRotation.EAST;
                }
                else if(this.direction == 6)
                {
                    moveDirection = RoomUserRotation.SOUTH;
                }
                else if(this.direction == 7)
                {
                    moveDirection = RoomUserRotation.WEST;
                }

                Tile newTile = new Tile(item.getX() + (moveDirection == RoomUserRotation.WEST ? -1 : (moveDirection == RoomUserRotation.EAST ? 1 : 0)), item.getY() + (moveDirection == RoomUserRotation.NORTH ? 1 : (moveDirection == RoomUserRotation.SOUTH ? -1 : 0)), item.getZ());

                if(room.tileWalkable(newTile))
                {
                    Rectangle rectangle = new Rectangle(newTile.x,
                            newTile.y,
                            item.getBaseItem().getWidth(),
                            item.getBaseItem().getLength());

                    for (int x = rectangle.x; x < rectangle.x + rectangle.getWidth(); x++)
                    {
                        for (int y = rectangle.y; y < rectangle.y + rectangle.getHeight(); y++)
                        {
                            HabboItem i = room.getTopItemAt(x, y, item);

                            if (i == null || i.getBaseItem().allowStack())
                            {
                                room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, room).compose());
                            }
                        }
                    }
                }
            }

            if(this.rotation > 0)
            {
                if(this.rotation == 1)
                {
                    item.setRotation(item.getRotation()+ 2);
                }
                else if(this.rotation == 2)
                {
                    item.setRotation(item.getRotation() + 6);
                }
                else if(this.rotation == 3)
                {
                    if(Emulator.getRandom().nextInt(2) == 1)
                    {
                        item.setRotation(item.getRotation() + 2);
                    }
                    else
                    {
                        item.setRotation(item.getRotation() + 6);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData()
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

        String data = this.direction + "\t" +
                this.rotation + "\t" +
                this.delay + "\t";

        for(HabboItem item : this.items)
        {
            data += item.getId() + "\r";
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<HabboItem>();

        String[] data = set.getString("wired_data").split("\t");

        if(data.length == 4)
        {
            try
            {
                this.direction = Integer.valueOf(data[0]);
                this.rotation = Integer.valueOf(data[1]);
                this.delay = Integer.valueOf(data[2]);
            }
            catch (Exception e){

            }

            for(String s : data[3].split("\r"))
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
        this.direction = 0;
        this.rotation = 0;
        this.delay = 0;
        this.items.clear();
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
        message.appendInt32(5);
        message.appendInt32(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt32(item.getId());
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(2);
            message.appendInt32(this.direction);
            message.appendInt32(this.rotation);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return false;

        packet.readInt();

        this.direction = packet.readInt();
        this.rotation = packet.readInt();

        packet.readString();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(room.getHabboItem(packet.readInt()));
        }

        this.delay = packet.readInt();

        return true;
    }
}
