package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 13-12-2014 16:40.
 */
public class RoomUserRemoveRightsComposer extends MessageComposer
{
    private final Room room;
    private final int habboId;

    public RoomUserRemoveRightsComposer(Room room, int habboId)
    {
        this.room = room;
        this.habboId = habboId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserRemoveRightsComposer);
        this.response.appendInt32(this.room.getId());
        this.response.appendInt32(this.habboId);
        return this.response;
    }
}
