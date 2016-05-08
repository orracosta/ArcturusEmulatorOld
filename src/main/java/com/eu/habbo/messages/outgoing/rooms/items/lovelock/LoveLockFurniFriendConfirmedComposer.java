package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-8-2015 08:37.
 */
public class LoveLockFurniFriendConfirmedComposer extends MessageComposer
{
    private InteractionLoveLock loveLock;

    public LoveLockFurniFriendConfirmedComposer(InteractionLoveLock loveLock)
    {
        this.loveLock = loveLock;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.LoveLockFurniFriendConfirmedComposer);
        this.response.appendInt32(this.loveLock.getId());
        return this.response;
    }
}
