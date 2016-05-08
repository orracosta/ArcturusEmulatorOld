package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 25-7-2015 14:59.
 */
public class MysticBoxCloseComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MysticBoxCloseComposer);

        return this.response;
    }
}
