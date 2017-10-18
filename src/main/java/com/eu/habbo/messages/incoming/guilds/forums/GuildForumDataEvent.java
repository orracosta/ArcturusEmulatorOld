package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;

public class GuildForumDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();

        this.client.sendResponse(new GuildForumDataComposer(Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId), this.client.getHabbo()));
    }
}