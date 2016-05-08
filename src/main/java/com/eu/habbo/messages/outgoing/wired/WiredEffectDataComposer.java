package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-12-2014 13:42.
 */
public class WiredEffectDataComposer extends MessageComposer
{
    private final InteractionWiredEffect effect;

    public WiredEffectDataComposer(InteractionWiredEffect effect)
    {
        this.effect = effect;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredEffectDataComposer);
        this.effect.serializeWiredData(this.response);
        return this.response;
    }
}
