package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.*;


public class GuildForumPostThreadEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt(); // if 0 == forum message != thread
        String subject = this.packet.readString();
        String message = this.packet.readString();

        //TODO: Add check if user has guild
        //TODO: Add check if threads can be posted.

        final GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);

        final GuildForumThread thread;
        if (forum != null)
        {
            if (threadId == 0)
            {
                thread = forum.createThread(this.client.getHabbo(), subject, message);

                GuildForumComment comment = new GuildForumComment(guildId, threadId, this.client.getHabbo().getHabboInfo().getId(),
                this.client.getHabbo().getHabboInfo().getUsername(), this.client.getHabbo().getHabboInfo().getLook(), message);
                thread.addComment(comment);

                this.client.sendResponse(new GuildForumThreadMessagesComposer(thread));
            }
            else
            {
                thread = forum.getThread(threadId);
                GuildForumComment comment = thread.addComment(this.client.getHabbo(), message);

                if (comment != null)
                {
                    this.client.sendResponse(new GuildForumAddCommentComposer(comment));
                }
                else
                {
                    //TODO Display error
                }
            }
        }
    }
}