package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildManageComposer;

/**
 * Created on 22-11-2014 22:36.
 */
public class RequestGuildManageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.packet.readInt());

        this.client.sendResponse(new GuildManageComposer(guild));
    }
}
