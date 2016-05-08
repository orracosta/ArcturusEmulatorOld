package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-11-2014 07:51.
 */
public class FriendRequestErrorComposer extends MessageComposer
{
    public static final int FRIEND_LIST_OWN_FULL = 1;
    public static final int TARGET_NOT_ACCEPTING_REQUESTS = 3;
    public static final int TARGET_NOT_FOUND = 4;

    private final int errorCode;

    public FriendRequestErrorComposer(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FriendRequestErrorComposer);
        this.response.appendInt32(0);
        this.response.appendInt32(this.errorCode);
        return this.response;
    }
}
