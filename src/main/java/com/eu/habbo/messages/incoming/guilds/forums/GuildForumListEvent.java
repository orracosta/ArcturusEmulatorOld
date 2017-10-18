package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumListComposer;
import gnu.trove.set.hash.THashSet;

public class GuildForumListEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int mode = this.packet.readInt();
        int page = this.packet.readInt();
        int amount = this.packet.readInt();

        switch(page)
        {
            case 0:
                    this.client.sendResponse(new GuildForumListComposer(Emulator.getGameEnvironment().getGuildForumManager().getGuildForums(this.client.getHabbo()), this.client.getHabbo(), mode));
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }
}