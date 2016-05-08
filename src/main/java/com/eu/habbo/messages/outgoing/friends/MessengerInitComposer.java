package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MessengerInitComposer extends MessageComposer
{
    private Habbo habbo;

    public MessengerInitComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MessengerInitComposer);
        this.response.appendInt32(300);
        this.response.appendInt32(1337);
        this.response.appendInt32(500);
        this.response.appendInt32(1000);
        this.response.appendInt32(0);
        return this.response;
    }
}
