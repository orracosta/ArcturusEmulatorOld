package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer1 extends MessageComposer
{
    public final int guildId;

    public UnknownGuildForumComposer1(int guildId)
    {
        this.guildId = guildId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildForumComposer1);
        this.response.appendInt32(this.guildId);
        return this.response;
    }
}