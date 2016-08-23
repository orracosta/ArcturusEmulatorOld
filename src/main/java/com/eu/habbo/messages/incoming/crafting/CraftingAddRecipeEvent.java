package com.eu.habbo.messages.incoming.crafting;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.crafting.CraftingRecipeComposer;

public class CraftingAddRecipeEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        System.out.println(this.getClass().getName());
        String recipeName = this.packet.readString();
        CraftingRecipe recipe = Emulator.getGameEnvironment().getCraftingManager().getRecipe(recipeName);

        if (recipe != null)
        {
            this.client.sendResponse(new CraftingRecipeComposer(recipe));
        }
    }
}
