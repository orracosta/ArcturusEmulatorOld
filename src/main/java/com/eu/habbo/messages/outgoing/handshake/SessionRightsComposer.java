package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class SessionRightsComposer extends MessageComposer
{

    private static final boolean unknownBooleanOne = true; //true
    private static final boolean unknownBooleanTwo = false;

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.SessionRightsComposer);

        this.response.appendBoolean(unknownBooleanOne);
        this.response.appendBoolean(true);
        this.response.appendBoolean(true);
        return this.response;
    }
}
