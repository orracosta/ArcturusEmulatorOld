package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-1-2016 15:50.
 */
public class CraftingResultComposer extends MessageComposer
{
    private final CraftingRecipe recipe;
    private final boolean succes;

    public CraftingResultComposer(CraftingRecipe recipe, boolean succes)
    {
        this.recipe = recipe;
        this.succes = succes;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftingResultComposer);

        this.response.appendBoolean(this.succes); //succes

        if(this.succes)
        {
            this.response.appendString(this.recipe.result);
            this.response.appendString(this.recipe.result);
        }

        return this.response;
    }
}
