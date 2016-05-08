package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-10-2015 21:43.
 */
public class GuideSessionRequesterRoomComposer extends MessageComposer
{
    private final Room room;

    //Sends you to the room.
    public GuideSessionRequesterRoomComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuideSessionRequesterRoomComposer);
        this.response.appendInt32(this.room != null ? this.room.getId() : 0);
        return this.response;
    }
}
