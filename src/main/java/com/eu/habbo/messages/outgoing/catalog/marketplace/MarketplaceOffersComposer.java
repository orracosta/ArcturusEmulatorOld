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
        this.response.appendInt(this.offers.size());

        for(MarketPlaceOffer offer : this.offers)
        {
            this.response.appendInt(offer.getOfferId());
            this.response.appendInt(1);
            this.response.appendInt(offer.getType());
            this.response.appendInt(offer.getItemId());
            if(offer.getType() == 3)
            {
                this.response.appendInt(offer.getLimitedNumber());
                this.response.appendInt(offer.getLimitedStack());
            }
            else if (offer.getType() == 2)
            {
                this.response.appendString("");
            }
            else
            {
                this.response.appendInt(0);
                this.response.appendString("");
            }
            this.response.appendInt(MarketPlace.calculateCommision(offer.getPrice()));
            this.response.appendInt(0);
            this.response.appendInt(MarketPlace.calculateCommision(offer.avarage));
            this.response.appendInt(offer.count);

            total += offer.count;
        }
        this.response.appendInt(total);
        return this.response;
    }
}
