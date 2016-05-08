package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 13-12-2014 21:59.
 */
public class WiredTriggerDataComposer extends MessageComposer
{
    private final InteractionWiredTrigger trigger;

    public WiredTriggerDataComposer(InteractionWiredTrigger trigger)
    {
        this.trigger = trigger;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredTriggerDataComposer);
        trigger.serializeWiredData(this.response);
        return this.response;
    }
}
