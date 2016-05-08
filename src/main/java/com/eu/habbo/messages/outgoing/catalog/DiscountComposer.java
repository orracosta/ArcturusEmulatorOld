package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-8-2014 15:25.
 */
public class DiscountComposer extends MessageComposer {
    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.DiscountComposer);

        this.response.appendInt32(100);
        this.response.appendInt32(6);
        this.response.appendInt32(1);
        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendInt32(40);
        this.response.appendInt32(99);

        return this.response;
    }
}
