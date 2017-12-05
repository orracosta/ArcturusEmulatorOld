package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DebugConsoleComposer extends MessageComposer
{

    private static final boolean debugging = true;

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.DebugConsoleComposer);

        this.response.appendBoolean(true);

        return this.response;
    }
}
