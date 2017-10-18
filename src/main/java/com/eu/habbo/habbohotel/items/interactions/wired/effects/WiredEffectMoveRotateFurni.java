package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectMoveRotateFurni extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.MOVE_ROTATE;

    private int direction;
    private int rotation;
    private final THashSet<HabboItem> items = new THashSet<HabboItem>();

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

        synchronized (this.items)
        {
            for (HabboItem item : this.items)
            {
                if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                    items.add(item);
            }

            for (HabboItem item : items)
            {
                this.items.remove(item);
            }

            for (HabboItem item : this.items)
            {
                if (this.rotation > 0)
                {
                    THashSet<RoomTile> tiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                    if (this.rotation == 1)
                    {
                        item.setRotation(item.getRotation() + 2);
                    }
                    else if (this.rotation == 2)
                    {
                        item.setRotation(item.getRotation() + 6);
                    }
                    else if (this.rotation == 3)
                    {
                        if (Emulator.getRandom().nextInt(2) == 1)
                        {
                            item.setRotation(item.getRotation() + 2);
                        }
                        else
                        {
                            item.setRotation(item.getRotation() + 6);
                        }
                    }

                    if (this.direction == 0)
                    {
                        tiles.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
                        room.updateTiles(tiles);
                        room.sendComposer(new FloorItemUpdateComposer(item).compose());
                    }
                }

                if (this.direction > 0)
                {
                    RoomUserRotation moveDirection = RoomUserRotation.NORTH;

                    if (this.direction == 1)
                    {
                        moveDirection = RoomUserRotation.values()[Emulator.getRandom().nextInt(RoomUserRotation.values().length)];
                    }
                    else if (this.direction == 2)
                    {
                        if (Emulator.getRandom().nextInt(2) == 1)
                        {
                            moveDirection = RoomUserRotation.EAST;
                        }
                        else
                        {
                            moveDirection = RoomUserRotation.WEST;
                        }
                    }
                    else if (this.direction == 3)
                    {
                        if (Emulator.getRandom().nextInt(2) == 1)
                        {
                            moveDirection = RoomUserRotation.NORTH;
                        }
                        else
                        {
                            moveDirection = RoomUserRotation.SOUTH;
                        }
                    }
                    else if (this.direction == 4)
                    {
                        moveDirection = RoomUserRotation.SOUTH;
                    }
                    else if (this.direction == 5)
                    {
                        moveDirection = RoomUserRotation.EAST;
                    }
                    else if (this.direction == 6)
                    {
                        moveDirection = RoomUserRotation.NORTH;
                    }
                    else if (this.direction == 7)
                    {
                        moveDirection = RoomUserRotation.WEST;
                    }

                    RoomTile newTile = room.getLayout().getTile(
                            (short) (item.getX() + ((moveDirection == RoomUserRotation.WEST || moveDirection == RoomUserRotation.NORTH_WEST || moveDirection == RoomUserRotation.SOUTH_WEST) ? -1 : (((moveDirection == RoomUserRotation.EAST || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.NORTH_EAST) ? 1 : 0)))),
                            (short) (item.getY() + ((moveDirection == RoomUserRotation.NORTH || moveDirection == RoomUserRotation.NORTH_EAST || moveDirection == RoomUserRotation.NORTH_WEST) ? 1 : ((moveDirection == RoomUserRotation.SOUTH || moveDirection == RoomUserRotation.SOUTH_EAST || moveDirection == RoomUserRotation.SOUTH_WEST) ? -1 : 0)))
                    );

                    if (newTile != null && room.tileWalkable(newTile))
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

                                if (i == null || i == item || i.getBaseItem().allowStack())
                                {
                                    double offset = room.getStackHeight(newTile.x, newTile.y, false) - item.getZ();
                                    room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, offset, room).compose());
                                }
                            }
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
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        this.items.remove(null);
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || (room != null && room.getHabboItem(item.getId()) == null))
                items.add(item);
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        String data = this.direction + "\t" +
                this.rotation + "\t" +
                this.getDelay() + "\t";

        for(HabboItem item : this.items)
        {
            data += item.getId() + "\r";
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        synchronized (this.items)
        {
            this.items.clear();

            String[] data = set.getString("wired_data").split("\t");

            if (data.length == 4)
            {
                try
                {
                    this.direction = Integer.valueOf(data[0]);
                    this.rotation = Integer.valueOf(data[1]);
                    this.setDelay(Integer.valueOf(data[2]));
                }
                catch (Exception e)
                {
                }

                for (String s : data[3].split("\r"))
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
        this.direction = 0;
        this.rotation = 0;
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return this.type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        synchronized (this.items)
        {
            for (HabboItem item : this.items)
            {
                if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                    items.add(item);
            }

            for (HabboItem item : items)
            {
                this.items.remove(item);
            }

            message.appendBoolean(false);
            message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
            message.appendInt(this.items.size());
            for (HabboItem item : this.items)
                message.appendInt(item.getId());
            message.appendInt(this.getBaseItem().getSpriteId());
            message.appendInt(this.getId());
            message.appendString("");
            message.appendInt(2);
            message.appendInt(this.direction);
            message.appendInt(this.rotation);
            message.appendInt(0);
            message.appendInt(this.getType().code);
            message.appendInt(this.getDelay());
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if(room == null)
            return false;

        packet.readInt();

        this.direction = packet.readInt();
        this.rotation = packet.readInt();

        packet.readString();

        int count = packet.readInt();

        synchronized (this.items)
        {
            this.items.clear();
            for (int i = 0; i < count; i++)
            {
                this.items.add(room.getHabboItem(packet.readInt()));
            }
        }

        this.setDelay(packet.readInt());

        return true;
    }
}
