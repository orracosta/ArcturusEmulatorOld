package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GiftReceiverNotFoundComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GiftReceiverNotFoundComposer);

        return this.response;
    }
}
