package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer8 extends MessageComposer
{
    public final GuildForum forum;
    public final int index;

    public UnknownGuildForumComposer8(GuildForum forum, int index)
    {
        this.forum = forum;
        this.index = index;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildForumComposer8);
        this.response.appendInt32(this.forum.getGuild().getId());
        this.response.appendInt32(this.index);
        this.forum.serializeThreads(this.response);
        return this.response;
    }
}