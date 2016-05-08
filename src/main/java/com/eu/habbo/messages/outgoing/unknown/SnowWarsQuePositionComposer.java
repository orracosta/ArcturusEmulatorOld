package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 18:52.
 */
public class SnowWarsQuePositionComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2077);
        this.response.appendInt32(1);
        return this.response;
    }
}
