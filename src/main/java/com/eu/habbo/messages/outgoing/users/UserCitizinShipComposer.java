package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 25-8-2014 12:53.
 */
public class UserCitizinShipComposer extends MessageComposer {

    private final String name;

    public UserCitizinShipComposer(String name)
    {
        this.name = name;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.UserCitizinShipComposer);
        this.response.appendString(this.name);
        this.response.appendInt32(0);
        this.response.appendInt32(0);

        return this.response;
    }
}
