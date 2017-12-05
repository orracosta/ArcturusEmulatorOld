package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;

public class TestInventoryEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        //this.client.sendResponse(new InventoryItemsComposer(this.client.getHabbo(), items));
    }
}
