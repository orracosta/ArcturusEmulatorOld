package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.ReloadRecyclerComposer;

/**
 * Created on 22-10-2014 10:36.
 */
public class ReloadRecyclerEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new ReloadRecyclerComposer());
    }
}
