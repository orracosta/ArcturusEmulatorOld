package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NewUserIdentityComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewUserIdentityComposer);
        this.response.appendInt(0);
        return this.response;
    }
}
