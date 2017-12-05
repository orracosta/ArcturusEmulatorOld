package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionDetachedComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuideSessionDetachedComposer);
        return this.response;
    }
}
