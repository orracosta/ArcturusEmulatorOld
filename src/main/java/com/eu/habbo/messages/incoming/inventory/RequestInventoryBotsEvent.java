package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryBotsComposer;

/**
 * Created on 28-8-2014 19:14.
 */
public class RequestInventoryBotsEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new InventoryBotsComposer(this.client.getHabbo()));
    }
}
