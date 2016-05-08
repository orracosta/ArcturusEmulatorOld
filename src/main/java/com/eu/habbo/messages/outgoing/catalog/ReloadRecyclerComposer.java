package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 22-10-2014 10:36.
 */
public class ReloadRecyclerComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ReloadRecyclerComposer);
        this.response.appendInt32(1);
        this.response.appendInt32(0);
        return this.response;
    }
}
