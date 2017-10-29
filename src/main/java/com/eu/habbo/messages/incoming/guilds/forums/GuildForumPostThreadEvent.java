package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.*;


public class GuildForumPostThreadEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt(); // if 0 == forum message != thread
        String subject = this.packet.readString();
        String message = this.packet.readString();

        final GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);
        final Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        final GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        boolean isStaff = this.client.getHabbo().hasPermission("acc_modtool_ticket_q");

        final GuildForumThread thread;

        if (forum != null) {
            if (message.length() < 10 || message.length() > 4000) return;

            if (threadId == 0) {
                if ((guild.canPostThreads().state == 0)
                        || (guild.canPostThreads().state == 1 && member != null)
                        || (guild.canPostThreads().state == 2 && member != null && (member.getRank() == GuildRank.MOD || member.getRank() == GuildRank.ADMIN))
                        || (guild.canPostThreads().state == 3 && guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId())
                        || isStaff) {
                    thread = forum.createThread(this.client.getHabbo(), subject, message);

                    GuildForumComment comment = new GuildForumComment(guildId, threadId, this.client.getHabbo().getHabboInfo().getId(),
                            this.client.getHabbo().getHabboInfo().getUsername(), this.client.getHabbo().getHabboInfo().getLook(), message);

                    thread.addComment(comment);

                    this.client.sendResponse(new GuildForumThreadMessagesComposer(thread));
                }
            } else {
                if ((guild.canPostThreads().state == 0)
                        || (guild.canPostThreads().state == 1 && member != null)
                        || (guild.canPostThreads().state == 2 && member != null && (member.getRank() == GuildRank.MOD || member.getRank() == GuildRank.ADMIN))
                        || (guild.canPostThreads().state == 3 && guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId())
                        || isStaff) {
                    thread = forum.getThread(threadId);

                    if(thread == null)
                        return;

                    GuildForumComment comment = thread.addComment(this.client.getHabbo(), message);

                    if (comment != null) {
                        this.client.sendResponse(new GuildForumAddCommentComposer(comment));
                    }
                }
            }
        }
    }
}