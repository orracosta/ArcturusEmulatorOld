package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumAddCommentComposer extends MessageComposer
{
    private final GuildForumComment comment;

    public GuildForumAddCommentComposer(GuildForumComment comment)
    {
        this.comment = comment;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumAddCommentComposer);
        this.response.appendInt(this.comment.getGuildId()); //guild_id
        this.response.appendInt(this.comment.getThreadId()); //thread_id
        this.comment.serialize(this.response); //Comment
        return this.response;
    }
}