package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceOffersComposer;

/**
 * Created on 1-11-2014 16:02.
 */
public class RequestOffersEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int min = this.packet.readInt();
        int max = this.packet.readInt();
        String query = this.packet.readString();
        int type = this.packet.readInt();

        this.client.sendResponse(new MarketplaceOffersComposer(MarketPlace.getOffers(min, max, query, type)));
    }
}
