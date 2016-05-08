package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 1-11-2014 12:14.
 */
public class MarketplaceOwnItemsComposer extends MessageComposer
{
    private Habbo habbo;

    public MarketplaceOwnItemsComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceOwnItemsComposer);
        this.response.appendInt32(habbo.getHabboInventory().getSoldPriceTotal());
        this.response.appendInt32(habbo.getHabboInventory().getMarketplaceItems().size());

        for(MarketPlaceOffer offer : habbo.getHabboInventory().getMarketplaceItems())
        {
            try
            {
                if (offer.getState() == MarketPlaceState.OPEN)
                {
                    if ((offer.getTimestamp() + 172800) - Emulator.getIntUnixTimestamp() <= 0)
                    {
                        offer.setState(MarketPlaceState.CLOSED);
                        Emulator.getThreading().run(offer);
                    }
                }

                this.response.appendInt32(offer.getOfferId());
                this.response.appendInt32(offer.getState().getState());
                this.response.appendInt32(offer.getType());
                this.response.appendInt32(offer.getItemId());

                if (offer.getType() == 3)
                {
                    this.response.appendInt32(offer.getLimitedNumber());
                    this.response.appendInt32(offer.getLimitedStack());
                } else
                {
                    this.response.appendInt32(0);
                    this.response.appendString("");
                }

                this.response.appendInt32(offer.getPrice() - (int) Math.ceil(offer.getPrice() / 100.0));

                if (offer.getState() == MarketPlaceState.OPEN)
                    this.response.appendInt32((((offer.getTimestamp() + 172800) - Emulator.getIntUnixTimestamp()) / 60));
                else
                    this.response.appendInt32(0);

                this.response.appendInt32(0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return this.response;
    }
}
