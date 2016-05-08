package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.GiftConfigurationComposer;

/**
 * Created on 27-8-2014 14:22.
 */
public class RequestGiftConfigurationEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new GiftConfigurationComposer());
    }
}
