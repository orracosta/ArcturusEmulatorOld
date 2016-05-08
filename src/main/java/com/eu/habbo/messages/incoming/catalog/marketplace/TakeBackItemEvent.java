package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 1-11-2014 14:26.
 */
public class TakeBackItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int offerId = this.packet.readInt();
        MarketPlace.takeBackItem(this.client.getHabbo(), offerId);
    }
}
