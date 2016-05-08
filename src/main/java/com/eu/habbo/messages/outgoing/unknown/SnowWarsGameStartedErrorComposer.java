package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 18:49.
 */
public class SnowWarsGameStartedErrorComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2860);
        this.response.appendInt32(1);
        return this.response;
    }
}
