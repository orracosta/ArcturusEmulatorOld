package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomRelativeMapComposer extends MessageComposer {

    private final Room room;

    public RoomRelativeMapComposer(Room room)
    {
        this.room = room;
    }
    
    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomRelativeMapComposer);
        this.response.appendInt32(this.room.getLayout().getMapSize() / this.room.getLayout().getMapSizeY());
        this.response.appendInt32(this.room.getLayout().getMapSize());
        for (int y = 0; y < this.room.getLayout().getMapSizeY(); y++) {
            for (int x = 0; x < this.room.getLayout().getMapSizeX(); x++) {
                this.response.appendShort(this.room.getLayout().getSquareStates()[x][y] == RoomTileState.BLOCKED ? 65535 : (int)this.room.getStackHeight(x, y, true));

            }
        }
        return this.response;
    }
}
