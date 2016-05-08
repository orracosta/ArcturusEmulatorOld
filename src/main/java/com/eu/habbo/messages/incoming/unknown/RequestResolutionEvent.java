package com.eu.habbo.messages.incoming.unknown;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.unknown.NewYearResolutionComposer;

/**
 * Created on 4-11-2014 12:48.
 */
public class RequestResolutionEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        int viewAll = this.packet.readInt();

        if(viewAll == 0)
        {
            this.client.sendResponse(new NewYearResolutionComposer());
        }
    }
}
