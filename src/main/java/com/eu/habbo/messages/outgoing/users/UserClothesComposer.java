package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserClothesComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserClothesComposer);
        this.response.appendInt32(1);
        this.response.appendInt32(3356);

        this.response.appendInt32(1);
        this.response.appendString("clothing_squid");

        return this.response;
    }
}
