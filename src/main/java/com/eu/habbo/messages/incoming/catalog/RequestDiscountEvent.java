package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.DiscountComposer;

/**
 * Created on 27-8-2014 14:20.
 */
public class RequestDiscountEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new DiscountComposer());
    }
}
