package com.eu.habbo.messages.outgoing.gamcenter.basejump;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BaseJumpJoinQueueComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BaseJumpJoinQueueComposer);
        this.response.appendInt32(3);
        return this.response;
    }
}