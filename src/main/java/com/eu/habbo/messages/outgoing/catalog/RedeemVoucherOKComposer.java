package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RedeemVoucherOKComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RedeemVoucherOKComposer);
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}
