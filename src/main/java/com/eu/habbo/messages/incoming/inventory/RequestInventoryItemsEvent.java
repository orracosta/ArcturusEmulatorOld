package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;

/**
 * Created on 28-8-2014 19:10.
 */
public class RequestInventoryItemsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new InventoryItemsComposer(this.client.getHabbo()));
    }
}
