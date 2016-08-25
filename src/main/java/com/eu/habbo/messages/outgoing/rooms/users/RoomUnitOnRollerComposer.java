package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.util.pathfinding.Tile;

public class RoomUnitOnRollerComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final HabboItem roller;
    private final Tile newLocation;
    private final Room room;

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, HabboItem roller, Tile newLocation, Room room)
    {
        this.roomUnit = roomUnit;
        this.roller = roller;
        this.newLocation = newLocation;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        if(!this.room.isLoaded())
            return null;

        if (!this.room.isAllowWalkthrough() && this.roller != null)
        {
            if (this.room.hasHabbosAt(this.newLocation.X, this.newLocation.Y))
            {
                return null;
            }
        }

        this.response.init(Outgoing.ObjectOnRollerComposer);
        this.response.appendInt32(this.roomUnit.getX());
        this.response.appendInt32(this.roomUnit.getY());
        this.response.appendInt32(this.newLocation.X);
        this.response.appendInt32(this.newLocation.Y);
        this.response.appendInt32(0);
        this.response.appendInt32(this.roller == null ? 0 : this.roller.getId());
        this.response.appendInt32(2);
        this.response.appendInt32(this.roomUnit.getId());
        this.response.appendString(this.roomUnit.getZ() + "");
        this.response.appendString(this.newLocation.Z + "");

        this.roomUnit.setLocation(this.newLocation.X, this.newLocation.Y, this.newLocation.Z);

        try
        {
            if(roller != null)
            {
                HabboItem item = this.room.getTopItemAt(this.newLocation.X, this.newLocation.Y);

                if(item != null)
                    item.onWalkOn(this.roomUnit, this.room, new Object[]{this.roller});
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return this.response;
    }
}
