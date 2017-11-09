package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUnitOnRollerComposer extends MessageComposer
{
    private final RoomUnit roomUnit;
    private final HabboItem roller;
    private final RoomTile newLocation;
    private final Room room;

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, HabboItem roller, RoomTile newLocation, Room room)
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
            if (this.room.hasHabbosAt(this.newLocation.x, this.newLocation.y))
            {
                return null;
            }
        }

        this.response.init(Outgoing.ObjectOnRollerComposer);
        this.response.appendInt32(this.roomUnit.getX());
        this.response.appendInt32(this.roomUnit.getY());
        this.response.appendInt32(this.newLocation.x);
        this.response.appendInt32(this.newLocation.y);
        this.response.appendInt(0);
        this.response.appendInt(this.roller == null ? 0 : this.roller.getId());
        this.response.appendInt(2);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendString(this.roomUnit.getZ() + "");
        this.response.appendString(this.newLocation.getStackHeight() + "");

        if(roller != null && this.roomUnit.getPath().isEmpty()){
            this.roomUnit.setCurrentLocation(room.getLayout().getTile(this.newLocation.x, this.newLocation.y));
            if(roomUnit.getPath().isEmpty() && newLocation != null)
            {
                Emulator.getThreading().run(new Runnable()
                {
                    @Override
                    public void run() {
                        if(roomUnit.getPath().isEmpty())
                            roomUnit.setLocation(room.getLayout().getTile(newLocation.x, newLocation.y));
                    }
                }, 380);
            }
        }
        else{
            roomUnit.setLocation(room.getLayout().getTile(newLocation.x, newLocation.y));
        }

        try
        {
            if(roller != null)
            {
                this.roller.onWalkOff(this.roomUnit, this.room, new Object[]{this.roller});

                Emulator.getThreading().run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        HabboItem item = room.getTopItemAt(newLocation.x, newLocation.y);
                        if(item != null)
                            try {
                                item.onWalkOn(roomUnit, room, new Object[]{roller});
                            } catch (Exception e)
                            {}
                    }
                }, 380);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
        this.roomUnit.sitUpdate = true;

        return this.response;
    }
}
