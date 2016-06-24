package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class DoorbellAddUserComposer extends MessageComposer
{
    private final Habbo habbo;

    public DoorbellAddUserComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.DoorbellAddUserComposer);
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        return this.response;
    }
}
