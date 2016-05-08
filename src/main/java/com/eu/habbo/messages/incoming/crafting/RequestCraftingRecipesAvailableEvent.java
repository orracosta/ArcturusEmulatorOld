package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftingComposerFour;

public class RequestCraftingRecipesAvailableEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println("Crafting: " + this.getClass().getName());
        int itemId = this.packet.readInt();

        int count = this.packet.readInt();
        for(int i = 0; i < count; i++)
        {
            this.packet.readInt();
        }

        this.client.sendResponse(new CraftingComposerFour());
    }
}
