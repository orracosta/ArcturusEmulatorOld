package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class LoveLockFurniFinishedComposer extends MessageComposer
{
    private InteractionLoveLock loveLock;

    public LoveLockFurniFinishedComposer(InteractionLoveLock loveLock)
    {
        this.loveLock = loveLock;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.LoveLockFurniFinishedComposer);
        this.response.appendInt(this.loveLock.getId());
        return this.response;
    }
}
