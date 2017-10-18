package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadMessagesComposer;


public class GuildForumModerateThreadEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int state = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);
        GuildForumThread thread = forum.getThread(threadId);
        thread.setState(GuildForum.ThreadState.fromValue(state));
        thread.setAdminId(this.client.getHabbo().getHabboInfo().getId());
        thread.setAdminName(this.client.getHabbo().getHabboInfo().getUsername());
        Emulator.getThreading().run(thread);

        this.client.sendResponse(new GuildForumThreadMessagesComposer(thread));
    }
}