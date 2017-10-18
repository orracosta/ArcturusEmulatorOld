package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class GuildForumCommentsComposer extends MessageComposer
{
    private final int guildId;
    private final int threadId;
    private final int index;
    private final List<GuildForumComment> guildForumCommentList;

    public GuildForumCommentsComposer(int guildId, int threadId, int index, List<GuildForumComment> guildForumCommentList)
    {
        this.guildId = guildId;
        this.threadId = threadId;
        this.index = index;
        this.guildForumCommentList = guildForumCommentList;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumCommentsComposer);
        this.response.appendInt(this.guildId); //guild_id
        this.response.appendInt(this.threadId); //thread_id
        this.response.appendInt(this.index); //start_index
        this.response.appendInt(this.guildForumCommentList.size());
        for (final GuildForumComment comment : this.guildForumCommentList)
        {
            comment.serialize(this.response);
        }
        return this.response;
    }
}