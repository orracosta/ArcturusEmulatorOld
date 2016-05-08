package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-11-2014 09:27.
 */
public class StalkErrorComposer extends MessageComposer
{
    public static final int NOT_IN_FRIEND_LIST = 0;
    public static final int FRIEND_OFFLINE = 1;
    public static final int FRIEND_NOT_IN_ROOM = 2;
    public static final int FRIEND_BLOCKED_STALKING = 3;

    private final int errorCode;

    public StalkErrorComposer(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.StalkErrorComposer);
        this.response.appendInt32(this.errorCode);
        return this.response;
    }
}
