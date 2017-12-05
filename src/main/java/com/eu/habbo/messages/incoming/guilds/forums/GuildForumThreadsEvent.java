package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadsComposer;

public class GuildForumThreadsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int groupdId = packet.readInt();
        int index = packet.readInt();

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(groupdId);

        if(forum == null)
            return;

        this.client.sendResponse(new GuildForumDataComposer(forum, this.client.getHabbo()));
        this.client.sendResponse(new GuildForumThreadsComposer(forum, index));

        //TODO read guild id;
    }
}