package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MutedWhisperComposer extends MessageComposer
{
    private int seconds;

    public MutedWhisperComposer(int seconds)
    {
        this.seconds = seconds;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MutedWhisperComposer);
        this.response.appendInt(this.seconds);
        return this.response;
    }
}
