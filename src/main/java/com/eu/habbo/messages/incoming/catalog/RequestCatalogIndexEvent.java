package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.CatalogPagesListComposer;

/**
 * Created on 27-8-2014 15:43.
 */
public class RequestCatalogIndexEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new CatalogPagesListComposer(this.client.getHabbo()));
    }
}
