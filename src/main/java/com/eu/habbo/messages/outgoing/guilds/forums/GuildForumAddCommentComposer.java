package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumAddCommentComposer extends MessageComposer
{
    private final GuildForumThread thread;
    private final GuildForumComment comment;

    public GuildForumAddCommentComposer(GuildForumThread thread, GuildForumComment comment)
    {
        this.thread = thread;
        this.comment = comment;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumAddCommentComposer);
        this.response.appendInt32(this.thread.getGuildId()); //guild_id
        this.response.appendInt32(this.thread.getId()); //thread_id
        this.comment.serialize(this.response); //Comment
        return this.response;
    }
}