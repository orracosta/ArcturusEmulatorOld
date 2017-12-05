package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class LoveLockFurniStartComposer extends MessageComposer
{
    private InteractionLoveLock loveLock;

    public LoveLockFurniStartComposer(InteractionLoveLock loveLock)
    {
        this.loveLock = loveLock;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.LoveLockFurniStartComposer);
        this.response.appendInt(this.loveLock.getId());
        this.response.appendBoolean(true);
        return this.response;
    }
}
