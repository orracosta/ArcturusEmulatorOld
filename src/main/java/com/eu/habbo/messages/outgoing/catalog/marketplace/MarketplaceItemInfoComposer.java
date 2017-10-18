package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MarketplaceItemInfoComposer extends MessageComposer
{
    private final int itemId;

    public MarketplaceItemInfoComposer(int itemId)
    {
        this.itemId = itemId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceItemInfoComposer);
        MarketPlace.serializeItemInfo(itemId, this.response);
        return this.response;
    }
}
