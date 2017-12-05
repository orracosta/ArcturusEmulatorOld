package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomMutedComposer extends MessageComposer
{
    private Room room;

    public RoomMutedComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomMutedComposer);
        this.response.appendBoolean(this.room.isMuted());
        return this.response;
    }
}
