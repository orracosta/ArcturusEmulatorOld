package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsUserExitArenaComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(3811);
        this.response.appendInt(1); //userId
        this.response.appendInt(1); //IDK ? TEAM?
        return this.response;
    }
}
