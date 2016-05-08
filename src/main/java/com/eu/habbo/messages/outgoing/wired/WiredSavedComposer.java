package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-12-2014 12:55.
 */
public class WiredSavedComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredSavedComposer);

        return this.response;
    }
}
