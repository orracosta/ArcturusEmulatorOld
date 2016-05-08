package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 16:26.
 */
public class UnknownComposer6 extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownComposer6);
        this.response.appendInt32(0);
        return this.response;
    }
}
