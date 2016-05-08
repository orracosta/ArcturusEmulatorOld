package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 17:29.
 */
public class SnowWarsOnStageEnding extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(1140);
        this.response.appendInt32(1); //idk
        return this.response;
    }
}
