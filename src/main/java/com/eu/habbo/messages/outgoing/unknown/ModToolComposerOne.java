package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-11-2014 13:40.
 */
public class ModToolComposerOne extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolComposerOne);


        return this.response;
    }
}
