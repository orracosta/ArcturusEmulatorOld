package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YouTradingDisabledComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.YouTradingDisabledComposer);
        return this.response;
    }
}