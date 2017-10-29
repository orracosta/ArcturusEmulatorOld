package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumComment;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.guilds.forums.PostUpdateMessageComposer;


public class GuildForumModerateMessageEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = packet.readInt();
        int threadId = packet.readInt();
        int messageId = packet.readInt();
        int state = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        boolean isStaff = this.client.getHabbo().hasPermission("acc_modtool_ticket_q");
        final GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        boolean isAdmin = member != null && (member.getRank() == GuildRank.MOD || member.getRank() == GuildRank.ADMIN || guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId());

        if (guild == null || (!isAdmin && !isStaff))
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);
        GuildForumThread thread = forum.getThread(threadId);

        if (thread != null) {
            GuildForumComment comment = thread.getComment(messageId);
            comment.setState(GuildForum.ThreadState.fromValue(state));
            comment.setAdminId(this.client.getHabbo().getHabboInfo().getId());
            comment.setAdminName(this.client.getHabbo().getHabboInfo().getUsername());

            switch (state) {
                case 10:
                case 20:
                    this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_MESSAGE_HIDDEN.key).compose());
                    break;
                case 1:
                    this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_MESSAGE_RESTORED.key).compose());
                    break;
            }

            Emulator.getThreading().run(comment);

            this.client.sendResponse(new PostUpdateMessageComposer(guildId, threadId, comment));

        } else {
            // TODO: throwing an error
        }
    }
}