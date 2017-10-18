package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import gnu.trove.set.hash.THashSet;

public class FloorItemOnRollerComposer extends MessageComposer
{
    private final HabboItem item;
    private final HabboItem roller;
    private final RoomTile newLocation;
    private final double heightOffset;
    private final Room room;

    public FloorItemOnRollerComposer(HabboItem item, HabboItem roller, RoomTile newLocation, double heightOffset, Room room)
    {
        this.item = item;
        this.roller = roller;
        this.newLocation = newLocation;
        this.heightOffset = heightOffset;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        short oldX = this.item.getX();
        short oldY = this.item.getY();

        this.response.init(Outgoing.ObjectOnRollerComposer);
        this.response.appendInt32(this.item.getX());
        this.response.appendInt32(this.item.getY());
        this.response.appendInt32(this.newLocation.x);
        this.response.appendInt32(this.newLocation.y);
        this.response.appendInt(1);
        this.response.appendInt(this.item.getId());
        this.response.appendString(Double.toString(this.item.getZ()));
        this.response.appendString(Double.toString(this.item.getZ() + this.heightOffset));
        this.response.appendInt(this.roller != null ? this.roller.getId() : -1);

        this.item.onMove(this.room, this.room.getLayout().getTile(item.getX(), item.getY()), this.newLocation);
        this.item.setX(this.newLocation.x);
        this.item.setY(this.newLocation.y);
        this.item.setZ(this.item.getZ() + this.heightOffset);
        this.item.needsUpdate(true);
        THashSet<RoomTile> tiles = this.room.getLayout().getTilesAt(this.room.getLayout().getTile(oldX, oldY), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation());
        tiles.addAll(this.room.getLayout().getTilesAt(this.room.getLayout().getTile(this.item.getX(), this.item.getY()), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation()));
        this.room.updateTiles(tiles);

        this.room.sendComposer(new UpdateStackHeightComposer(oldX, oldY, this.room.getStackHeight(oldX, oldY, true)).compose());

        this.room.updateHabbosAt(RoomLayout.getRectangle(this.item.getX(), this.item.getY(), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation()));

        return this.response;
    }
}
