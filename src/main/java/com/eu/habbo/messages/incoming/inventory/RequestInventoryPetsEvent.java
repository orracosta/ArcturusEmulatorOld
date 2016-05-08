package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryPetsComposer;

/**
 * Created on 28-8-2014 19:13.
 */
public class RequestInventoryPetsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new InventoryPetsComposer(this.client.getHabbo()));
    }
}
