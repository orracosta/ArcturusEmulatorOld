package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 10:16.
 */
public class UnknownComposer5 extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownComposer5);
        this.response.appendInt32(0);
        return this.response;
    }
}
