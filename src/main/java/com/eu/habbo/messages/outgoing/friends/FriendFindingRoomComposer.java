package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 13:46.
 */
public class FriendFindingRoomComposer extends MessageComposer
{
    public static final int NO_ROOM_FOUND = 0;
    public static final int ROOM_FOUND = 1;

    private int errorCode;

    public FriendFindingRoomComposer(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FriendFindingRoomComposer);
        this.response.appendInt32(this.errorCode);
        return this.response;
    }
}
