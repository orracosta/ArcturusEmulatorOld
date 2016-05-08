package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 27-3-2015 19:14.
 */
public class KnockKnockUnknownComposer extends MessageComposer
{
    private final Habbo habbo;

    public KnockKnockUnknownComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(478); //TODO Hardcoded header
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendInt32(this.habbo.getHabboInfo().getId());
        return this.response;
    }
}
