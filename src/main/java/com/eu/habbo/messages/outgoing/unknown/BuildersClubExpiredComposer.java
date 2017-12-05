package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BuildersClubExpiredComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BuildersClubExpiredComposer);
        this.response.appendInt(Integer.MAX_VALUE);
        this.response.appendInt(0);
        this.response.appendInt(100);
        this.response.appendInt(100000);
        this.response.appendInt(0);
        return this.response;
    }
}
