package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumAddCommentComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadMessagesComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.UnknownGuildForumComposer6;


public class GuildForumModerateMessageEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int messageId = packet.readInt();
        int state = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);
        GuildForumThread thread = forum.getThread(threadId);

        if (thread != null)
        {
          GuildForumComment comment =  thread.getComment(messageId);
          comment.setState(GuildForum.ThreadState.fromValue(state));
          comment.setAdminId(this.client.getHabbo().getHabboInfo().getId());
          comment.setAdminName(this.client.getHabbo().getHabboInfo().getUsername());
          Emulator.getThreading().run(comment);

          this.client.sendResponse(new UnknownGuildForumComposer6(guildId, threadId, comment));

        } else
        {
            // TODO: throwing an error
        }
    }
}