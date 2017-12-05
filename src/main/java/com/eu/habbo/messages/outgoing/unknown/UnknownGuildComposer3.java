package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildComposer3 extends MessageComposer
{
    private final int userId;

    public UnknownGuildComposer3(int userId)
    {
        this.userId = userId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildComposer3);
        this.response.appendInt(this.userId);
        return this.response;
    }
}