package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 19:17.
 */
public class SnowWarsRemoveUserComposer extends MessageComposer
{
    /*
        Removes a user from the "Waiting for users" window.
        //Works
     */
    @Override
    public ServerMessage compose()
    {
        this.response.init(2502);
        this.response.appendInt32(3);
        return this.response;
    }
}
