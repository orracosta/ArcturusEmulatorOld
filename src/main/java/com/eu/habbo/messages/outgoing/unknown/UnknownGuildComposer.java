package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildComposer extends MessageComposer
{
    private final int guildId;
    private final int requester;

    //Related to membership requess?
    public UnknownGuildComposer(int guildId, int requester)
    {
        this.guildId = guildId;
        this.requester = requester;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildComposer);
        this.response.appendInt(this.guildId);
        this.response.appendInt(this.requester);
        return this.response;
    }
}