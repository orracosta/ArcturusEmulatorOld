package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MinimailCountComposer extends MessageComposer
{

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MinimailCountComposer);
        this.response.appendInt(0);

        return this.response;
    }
}
