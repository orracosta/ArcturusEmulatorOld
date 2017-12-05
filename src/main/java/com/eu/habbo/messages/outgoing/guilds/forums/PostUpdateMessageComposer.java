package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PostUpdateMessageComposer extends MessageComposer
{
    public final int guildId;
    public final int threadId;
    public final GuildForumComment comment;

    public PostUpdateMessageComposer(int guildId, int threadId, GuildForumComment comment)
    {
        this.guildId = guildId;
        this.threadId = threadId;
        this.comment = comment;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PostUpdateMessageComposer);

        this.response.appendInt(this.guildId); //guild_id
        this.response.appendInt(this.threadId); //thread_id
        this.comment.serialize(this.response); //Comment

        return this.response;
    }
}