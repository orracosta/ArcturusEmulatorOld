package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 10:12.
 */
public class UnknownComposer4 extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownComposer4);
        this.response.appendBoolean(false); //Think something related to promo. Not sure though.
        return this.response;
    }
}
