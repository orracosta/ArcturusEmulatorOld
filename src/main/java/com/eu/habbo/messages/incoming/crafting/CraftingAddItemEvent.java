package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 10-1-2016 16:58.
 */
public class CraftingAddItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println("Crafting: " + this.getClass().getName());
        String itemName = this.packet.readString();
    }
}
