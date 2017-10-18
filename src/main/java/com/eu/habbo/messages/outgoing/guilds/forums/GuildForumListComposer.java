package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class GuildForumListComposer extends MessageComposer
{
    private final List<GuildForum> forums;
    private final Habbo habbo;
    private final int viewMode;

    public GuildForumListComposer(List<GuildForum> forums, Habbo habbo, int viewMode)
    {
        this.forums   = forums;
        this.habbo    = habbo;
        this.viewMode = viewMode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumListComposer);
        this.response.appendInt(this.viewMode);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(this.forums.size()); //Count...
        for (final GuildForum forum  : this.forums)
        {
            forum.serializeUserForum(this.response, this.habbo);
        }

        return this.response;
    }
}