package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumThreadsComposer extends MessageComposer
{
    public final GuildForum forum;
    public final int index;

    public GuildForumThreadsComposer(GuildForum forum, int index)
    {
        this.forum = forum;
        this.index = index;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumThreadsComposer);
        this.response.appendInt(this.forum.getGuild());
        this.response.appendInt(this.index);
        this.forum.serializeThreads(this.response);
        return this.response;
    }
}