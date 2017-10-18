package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumCommentsComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;

public class GuildForumThreadsMessagesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int index = packet.readInt(); //40
        int limit = packet.readInt(); //20

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if(guild == null)
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);
        GuildForumThread thread = forum.getThread(threadId);


        if (thread.getState() == GuildForum.ThreadState.HIDDEN_BY_ADMIN && guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
        {
            this.client.sendResponse(new BubbleAlertComposer("forums.error.access_denied"));
        } else
        {
            this.client.sendResponse(new GuildForumCommentsComposer(guildId, threadId, index, thread.getComments(index, limit)));
        }

        this.client.sendResponse(new GuildForumDataComposer(Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId), this.client.getHabbo()));
    }
}