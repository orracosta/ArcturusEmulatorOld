package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class WiredConditionDataComposer extends MessageComposer
{
    private final InteractionWiredCondition condition;

    public WiredConditionDataComposer(InteractionWiredCondition condition)
    {
        this.condition = condition;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredConditionDataComposer);
        this.condition.serializeWiredData(this.response);
        this.condition.needsUpdate(true);
        return this.response;
    }
}
