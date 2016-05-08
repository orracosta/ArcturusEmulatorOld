package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 23-10-2014 13:38.
 */
public class KnockKnockComposer extends MessageComposer
{
    private final Habbo habbo;

    public KnockKnockComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.KnockKnockComposer);
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        return this.response;
    }
}
