package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceItemInfoComposer;

/**
 * Created on 2-11-2014 08:44.
 */
public class RequestItemInfoEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.packet.readInt();
        int id = this.packet.readInt();

        this.client.sendResponse(new MarketplaceItemInfoComposer(id));
    }
}
