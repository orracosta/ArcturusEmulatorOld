package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.PongComposer;

/**
 * Created on 27-7-2015 13:15.
 */
public class PingEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new PongComposer(this.packet.readInt()));
    }
}
