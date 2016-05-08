package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 17:15.
 */
public class SnowWarsStartLobbyCounter extends MessageComposer
{

    @Override
    public ServerMessage compose()
    {
        this.response.init(3757);
        this.response.appendInt32(5);
        return this.response;
    }
}
