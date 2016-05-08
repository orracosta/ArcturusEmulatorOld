package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 18:47.
 */
public class SnowWarsPreviousRoomComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(1381);
        this.response.appendInt32(1); //room Id
        return this.response;
    }
}
