package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer6 extends MessageComposer
{
    public final int guildId;
    public final int threadId;
    public final GuildForumComment comment;

    public UnknownGuildForumComposer6(int guildId, int threadId, GuildForumComment comment)
    {
        this.guildId = guildId;
        this.threadId = threadId;
        this.comment = comment;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildForumComposer6);
        this.response.appendInt(this.guildId); //guild_id
        this.response.appendInt(this.threadId); //thread_id
        this.comment.serialize(this.response); //Comment
        return this.response;
    }
}