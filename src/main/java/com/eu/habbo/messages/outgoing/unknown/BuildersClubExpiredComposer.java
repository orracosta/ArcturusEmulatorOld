package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-6-2015 10:20.
 */
public class BuildersClubExpiredComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BuildersClubExpiredComposer);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(100);
        this.response.appendInt32(100000);
        this.response.appendInt32(0);
        return this.response;
    }
}
