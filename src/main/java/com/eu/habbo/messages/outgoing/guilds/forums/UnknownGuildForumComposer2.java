package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer2 extends MessageComposer
{
    public final GuildForumThread thread;

    public UnknownGuildForumComposer2(GuildForumThread thread)
    {
        this.thread = thread;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownGuildForumComposer2);
        this.response.appendInt32(this.thread.getGuildId());
        this.thread.serialize(this.response);
        return this.response;
    }
}