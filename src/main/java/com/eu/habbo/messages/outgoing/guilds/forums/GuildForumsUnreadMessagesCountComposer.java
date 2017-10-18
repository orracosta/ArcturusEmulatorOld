package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumsUnreadMessagesCountComposer extends MessageComposer
{
    public final int count;

    public GuildForumsUnreadMessagesCountComposer(int count)
    {
        this.count = count;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumsUnreadMessagesCountComposer);
        this.response.appendInt(this.count);
        return this.response;
    }
}