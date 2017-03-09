package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class MarketplaceOffersComposer extends MessageComposer
{
    private final THashSet<MarketPlaceOffer> offers;

    public MarketplaceOffersComposer(THashSet<MarketPlaceOffer> offers)
    {
        this.offers = offers;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceOffersComposer);
        int total = 0;
        this.response.appendInt32(this.offers.size());

        for(MarketPlaceOffer offer : this.offers)
        {
            this.response.appendInt32(offer.getOfferId());
            this.response.appendInt32(1);
            this.response.appendInt32(offer.getType());
            this.response.appendInt32(offer.getItemId());
            if(offer.getType() == 3)
            {
                this.response.appendInt32(offer.getLimitedNumber());
                this.response.appendInt32(offer.getLimitedStack());
            }
            else
            {
                this.response.appendInt32(0);
                this.response.appendString("");
            }
            this.response.appendInt32(MarketPlace.calculateCommision(offer.getPrice()));
            this.response.appendInt32(0);
            this.response.appendInt32(offer.avarage);
            this.response.appendInt32(offer.count);

            total += offer.count;
        }
        this.response.appendInt32(total);
        return this.response;
    }
}
