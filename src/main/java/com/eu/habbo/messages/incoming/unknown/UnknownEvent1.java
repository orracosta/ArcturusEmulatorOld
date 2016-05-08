package com.eu.habbo.messages.incoming.unknown;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.unknown.UnknownComposer6;

/**
 * Created on 17-6-2015 16:25.
 */
public class UnknownEvent1 extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new UnknownComposer6());
    }
}
