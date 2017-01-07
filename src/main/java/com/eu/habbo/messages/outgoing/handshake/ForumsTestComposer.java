package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ForumsTestComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2379);
        this.response.appendInt32(0);
        return this.response;
    }
}