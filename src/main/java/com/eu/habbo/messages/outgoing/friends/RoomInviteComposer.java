package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 14:03.
 */
public class RoomInviteComposer extends MessageComposer
{
    private int userId;
    private String message;

    public RoomInviteComposer(int userId, String message)
    {
        this.userId = userId;
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomInviteComposer);
        this.response.appendInt32(this.userId);
        this.response.appendString(this.message);
        return this.response;
    }
}
