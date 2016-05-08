package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-8-2015 08:36.
 */
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
        this.response.appendInt32(this.loveLock.getId());
        this.response.appendBoolean(true);
        return this.response;
    }
}
