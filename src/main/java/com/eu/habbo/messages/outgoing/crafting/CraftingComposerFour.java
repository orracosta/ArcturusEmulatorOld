package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CraftingComposerFour extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CraftingComposerFour);
        this.response.appendInt32(0); //count?
        this.response.appendBoolean(false); //idk
        return this.response;
    }
}
