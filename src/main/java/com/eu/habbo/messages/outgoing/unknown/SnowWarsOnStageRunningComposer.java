package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 22:25.
 */
public class SnowWarsOnStageRunningComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(3832);
        this.response.appendInt32(120);
        return this.response;
    }
}
