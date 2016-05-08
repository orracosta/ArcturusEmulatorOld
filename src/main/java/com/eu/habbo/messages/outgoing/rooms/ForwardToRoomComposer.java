package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 13:48.
 */
public class ForwardToRoomComposer extends MessageComposer
{
    private int roomId;

    public ForwardToRoomComposer(int roomId)
    {
        this.roomId = roomId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ForwardToRoomComposer);
        this.response.appendInt32(this.roomId);
        return this.response;
    }
}
