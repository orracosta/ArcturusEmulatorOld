package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsInitGameArena extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(3924);
        this.response.appendInt(0);
        return this.response;
    }
}
