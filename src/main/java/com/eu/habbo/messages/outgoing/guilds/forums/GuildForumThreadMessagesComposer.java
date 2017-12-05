package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumThreadMessagesComposer extends MessageComposer
{
    public final GuildForumThread thread;

    public GuildForumThreadMessagesComposer(GuildForumThread thread)
    {
        this.thread = thread;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumThreadMessagesComposer);
        this.response.appendInt(this.thread.getGuildId());
        this.thread.serialize(this.response);
        return this.response;
    }
}