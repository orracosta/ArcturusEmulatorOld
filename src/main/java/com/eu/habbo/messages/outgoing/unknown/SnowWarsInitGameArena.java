package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:49.
 */
public class SnowWarsInitGameArena extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(3924);
        this.response.appendInt32(0);
        return this.response;
    }
}
