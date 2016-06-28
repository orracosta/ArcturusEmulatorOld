package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

public class FloorItemOnRollerComposer extends MessageComposer
{
    private final HabboItem item;
    private final HabboItem roller;
    private final Tile newLocation;
    private final Room room;

    public FloorItemOnRollerComposer(HabboItem item, HabboItem roller, Tile newLocation, Room room)
    {
        this.item = item;
        this.roller = roller;
        this.newLocation = newLocation;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        int oldX = this.item.getX();
        int oldY = this.item.getY();

        this.response.init(Outgoing.ObjectOnRollerComposer);
        this.response.appendInt32(this.item.getX());
        this.response.appendInt32(this.item.getY());
        this.response.appendInt32(this.newLocation.X);
        this.response.appendInt32(this.newLocation.Y);
        this.response.appendInt32(1);
        this.response.appendInt32(this.item.getId());
        this.response.appendString(Double.toString(this.item.getZ()));
        this.response.appendString(Double.toString(this.newLocation.Z));
        this.response.appendInt32(this.roller != null ? this.roller.getId() : -1);

        item.setX(this.newLocation.X);
        item.setY(this.newLocation.Y);
        item.setZ(this.newLocation.Z);
        item.onMove(room, this.item.getLocation(), this.newLocation);
        item.needsUpdate(true);

        this.room.sendComposer(new UpdateStackHeightComposer(oldX, oldY, this.room.getStackHeight(oldX, oldY, true)).compose());

        this.room.updateHabbosAt(PathFinder.getSquare(this.item.getX(), this.item.getY(), this.item.getBaseItem().getWidth(), this.item.getBaseItem().getLength(), this.item.getRotation()));

        return this.response;
    }
}
