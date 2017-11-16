package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GuildForumListComposer extends MessageComposer {
    private final List<GuildForum> forums;
    private final Habbo habbo;
    private final int viewMode;
    private final int startIndex;

    public GuildForumListComposer(List<GuildForum> forums, Habbo habbo, int viewMode, int page) {
        this.forums = forums;
        this.habbo = habbo;
        this.viewMode = viewMode;
        this.startIndex = page;
    }

    @Override
    public ServerMessage compose() {
        forums.removeIf(Objects::isNull);

        List<Integer> guilds = forums.stream().skip(this.startIndex).limit(20).map(GuildForum::getGuild).collect(Collectors.toList());

        this.response.init(Outgoing.GuildForumListComposer);
        this.response.appendInt(this.viewMode);
        this.response.appendInt(guilds.size());
        this.response.appendInt(0);
        this.response.appendInt(this.forums.size());
        for (final GuildForum forum : this.forums) {
            forum.serializeUserForum(this.response, this.habbo);
        }

        return this.response;
    }
}