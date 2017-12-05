package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class WiredEffectDataComposer extends MessageComposer
{
    private final InteractionWiredEffect effect;
    private final Room room;

    public WiredEffectDataComposer(InteractionWiredEffect effect, Room room)
    {
        this.effect = effect;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredEffectDataComposer);
        this.effect.serializeWiredData(this.response, room);
        this.effect.needsUpdate(true);
        return this.response;
    }
}
