package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DiscountComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.DiscountComposer);

        this.response.appendInt(100);
        this.response.appendInt(6);
        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendInt(2);
        this.response.appendInt(40);
        this.response.appendInt(99);

        return this.response;
    }
}
