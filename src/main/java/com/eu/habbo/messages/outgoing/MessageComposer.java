package com.eu.habbo.messages.outgoing;

import com.eu.habbo.messages.ServerMessage;

public abstract class MessageComposer
{
    protected final ServerMessage response;
    
    protected MessageComposer()
    {
        this.response = new ServerMessage();
    }
    
    public abstract ServerMessage compose();
}