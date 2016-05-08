package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 13-12-2014 10:35.
 */
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

        this.roomUnit.setX(newLocation.X);
        this.roomUnit.setY(newLocation.Y);
        this.roomUnit.setZ(newLocation.Z);

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
