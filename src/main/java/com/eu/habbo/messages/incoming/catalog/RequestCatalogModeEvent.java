package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.CatalogModeComposer;
import com.eu.habbo.messages.outgoing.catalog.CatalogPagesListComposer;

public class RequestCatalogModeEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception {

        if(this.packet.readString().equalsIgnoreCase("normal"))
        {
            this.client.sendResponse(new CatalogModeComposer(0));
            this.client.sendResponse(new CatalogPagesListComposer(this.client.getHabbo()));
        }
        else
        {
            this.client.sendResponse(new CatalogModeComposer(1));
        }

    }
}
