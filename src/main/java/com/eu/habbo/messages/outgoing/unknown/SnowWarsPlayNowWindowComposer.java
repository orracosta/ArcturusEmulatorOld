package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:35.
 */
public class SnowWarsPlayNowWindowComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2276);
        this.response.appendInt32(0); //status
        this.response.appendInt32(100);
        this.response.appendInt32(0);
        this.response.appendInt32(-1);
        return this.response;
    }
}
