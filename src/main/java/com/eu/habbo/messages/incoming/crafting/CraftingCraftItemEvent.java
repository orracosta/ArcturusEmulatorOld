package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 16-1-2016 15:48.
 */
public class CraftingCraftItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println("Crafting: " + this.getClass().getName());
        this.packet.readInt();
        this.packet.readString();
    }
}
