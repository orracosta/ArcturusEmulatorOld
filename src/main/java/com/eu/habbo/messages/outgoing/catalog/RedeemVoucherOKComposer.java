package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 30-8-2014 13:45.
 */
public class RedeemVoucherOKComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RedeemVoucherOKComposer);
        this.response.appendString("");
        this.response.appendString("");
        return this.response;
    }
}
