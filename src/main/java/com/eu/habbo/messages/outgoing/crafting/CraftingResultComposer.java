package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CraftingResultComposer extends MessageComposer
{
    private final CraftingRecipe recipe;

    public CraftingResultComposer(CraftingRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftingResultComposer);

        this.response.appendBoolean(this.recipe != null); //succes

        if(this.recipe != null)
        {
            this.response.appendString(this.recipe.getName());
            this.response.appendString(this.recipe.getReward().getName());
        }

        return this.response;
    }
}
