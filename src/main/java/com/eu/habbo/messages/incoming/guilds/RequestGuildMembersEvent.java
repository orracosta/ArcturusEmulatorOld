package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildMembersComposer;

public class RequestGuildMembersEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int groupId = this.packet.readInt();
        int pageId = this.packet.readInt();
        String query = this.packet.readString();
        int levelId = this.packet.readInt();

        Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(groupId);
        this.client.sendResponse(new GuildMembersComposer(g, Emulator.getGameEnvironment().getGuildManager().getGuildMembers(g, pageId, levelId, query), this.client.getHabbo(), pageId, levelId));

    }
}
