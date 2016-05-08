package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 13-12-2014 17:14.
 */
public class RoomAddRightsListComposer extends MessageComposer
{
    private final Room room;
    private final int userId;
    private final String userName;

    public RoomAddRightsListComposer(Room room, int userId, String username)
    {
        this.room = room;
        this.userId = userId;
        this.userName = username;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomAddRightsListComposer);
        this.response.appendInt32(this.room.getId());
        this.response.appendInt32(this.userId);
        this.response.appendString(this.userName);
        return this.response;
    }
}
