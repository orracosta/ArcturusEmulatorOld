package com.eu.habbo.messages.incoming.catalog.recycler;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RecyclerLogicComposer;

/**
 * Created on 27-8-2014 14:15.
 */
public class RequestRecyclerLogicEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new RecyclerLogicComposer());
    }
}
