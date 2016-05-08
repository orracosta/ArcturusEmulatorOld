package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 1-11-2014 15:13.
 */
public class MarketplaceCancelSaleComposer extends MessageComposer
{
    private final MarketPlaceOffer offer;
    private final boolean success;

    public MarketplaceCancelSaleComposer(MarketPlaceOffer offer, Boolean success)
    {
        this.offer = offer;
        this.success = success;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceCancelSaleComposer);
        this.response.appendInt32(this.offer.getOfferId());
        this.response.appendBoolean(this.success);
        return this.response;
    }
}
