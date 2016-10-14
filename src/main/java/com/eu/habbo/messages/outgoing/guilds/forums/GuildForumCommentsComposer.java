package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumCommentsComposer extends MessageComposer
{
    private final GuildForumThread thread;
    private final int index;

    public GuildForumCommentsComposer(GuildForumThread thread, int index)
    {
        this.thread = thread;
        this.index = index;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumCommentsComposer);
        this.response.appendInt32(this.thread.getGuildId()); //guild_id
        this.response.appendInt32(this.thread.getId()); //thread_id
        this.response.appendInt32(this.index); //start_index
        this.thread.serializeComments(this.response, index, 20);
        return this.response;
    }
}