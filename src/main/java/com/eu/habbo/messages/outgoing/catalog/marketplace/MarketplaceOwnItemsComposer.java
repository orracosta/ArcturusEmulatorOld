package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

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
        this.response.appendInt(habbo.getInventory().getSoldPriceTotal());
        this.response.appendInt(habbo.getInventory().getMarketplaceItems().size());

        for(MarketPlaceOffer offer : habbo.getInventory().getMarketplaceItems())
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

                this.response.appendInt(offer.getOfferId());
                this.response.appendInt(offer.getState().getState());
                this.response.appendInt(offer.getType());
                this.response.appendInt(offer.getItemId());

                if (offer.getType() == 3)
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

                this.response.appendInt(offer.getPrice());

                if (offer.getState() == MarketPlaceState.OPEN)
                    this.response.appendInt((((offer.getTimestamp() + 172800) - Emulator.getIntUnixTimestamp()) / 60));
                else
                    this.response.appendInt(0);

                this.response.appendInt(0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return this.response;
    }
}
