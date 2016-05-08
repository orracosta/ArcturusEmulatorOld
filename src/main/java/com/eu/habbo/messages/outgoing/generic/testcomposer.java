package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class testcomposer extends MessageComposer
{
    @Override
    public ServerMessage compose() {
        this.response.init(3019);
        this.response.appendInt32(3);

        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendString("Key");

        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendString("Key");

        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendString("Key");
        this.response.appendBoolean(true);
        this.response.appendInt32(1);
        return this.response;
    }
}
