package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomRelativeMapComposer extends MessageComposer
{

    private final Room room;

    public RoomRelativeMapComposer(Room room)
    {
        this.room = room;
    }
    
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomRelativeMapComposer);
        this.response.appendInt(this.room.getLayout().getMapSize() / this.room.getLayout().getMapSizeY());
        this.response.appendInt(this.room.getLayout().getMapSize());
        for (short y = 0; y < this.room.getLayout().getMapSizeY(); y++)
        {
            for (short x = 0; x < this.room.getLayout().getMapSizeX(); x++)
            {
                RoomTile t = this.room.getLayout().getTile(x, y);

                if (t != null)
                    this.response.appendShort(t.relativeHeight());
                else
                    this.response.appendShort(Short.MAX_VALUE);

            }
        }
        return this.response;
    }
}
