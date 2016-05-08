package com.eu.habbo.messages.outgoing.events.mysticbox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 25-7-2015 15:02.
 */
public class MysticBoxPrizeComposer extends MessageComposer
{
    private String type;
    private int itemId;
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MysticBoxPrizeComposer);
        this.response.appendString(this.type);
        this.response.appendInt32(this.itemId);
        return this.response;
    }
}
