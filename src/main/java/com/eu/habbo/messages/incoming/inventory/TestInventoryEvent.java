package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;

/**
 * Created on 10-1-2016 22:02.
 */
public class TestInventoryEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new InventoryItemsComposer(this.client.getHabbo()));
    }
}
