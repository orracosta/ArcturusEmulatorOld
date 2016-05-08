package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 29-12-2014 20:00.
 */
public class SnowWarsUserExitArenaComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(3811);
        this.response.appendInt32(1); //userId
        this.response.appendInt32(1); //IDK ? TEAM?
        return this.response;
    }
}
