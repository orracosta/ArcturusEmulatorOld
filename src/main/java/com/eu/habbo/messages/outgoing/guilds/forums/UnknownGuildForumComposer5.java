package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer5 extends MessageComposer
{
    public final GuildForumThread thread;

    public UnknownGuildForumComposer5(GuildForumThread thread)
    {
        this.thread = thread;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildForumComposer5);
        this.response.appendInt32(this.thread.getGuildId()); //guild_id
        this.response.appendInt32(this.thread.getId()); //thread_id
        this.response.appendInt32(this.thread.getStartIndex()); //start_index
        this.thread.serializeComments(this.response);
        return this.response;
    }
}