package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 27-3-2015 21:04.
 */
public class KnockKnockUnknownComposer2 extends MessageComposer
{
    private final Habbo habbo;

    public KnockKnockUnknownComposer2(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(1504);//TODO Hardcoded header
        if(this.habbo != null)
        {
            this.response.appendString(this.habbo.getHabboInfo().getUsername());
        }
        this.response.appendString("");
        return this.response;
    }
}
