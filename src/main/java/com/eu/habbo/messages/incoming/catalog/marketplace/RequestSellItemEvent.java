package com.eu.habbo.messages.incoming.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceSellItemComposer;

/**
 * Created on 2-11-2014 11:38.
 */
public class RequestSellItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(Emulator.getConfig().getBoolean("hotel.marketplace.enabled"))
            this.client.sendResponse(new MarketplaceSellItemComposer(1, 0, 0));
        else
            this.client.sendResponse(new MarketplaceSellItemComposer(3, 0, 0));
    }
}
