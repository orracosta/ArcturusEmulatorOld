package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 16-1-2016 15:52.
 */
public class CraftingCraftSecretEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println("Crafting: " + this.getClass().getName());
        int something = this.packet.readInt();
        int count = this.packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.packet.readInt();
        }
    }
}
