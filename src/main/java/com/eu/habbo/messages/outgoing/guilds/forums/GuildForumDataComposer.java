package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumDataComposer extends MessageComposer {
    public final GuildForum forum;
    public Habbo habbo;

    public GuildForumDataComposer(GuildForum forum, Habbo habbo) {
        this.forum = forum;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.GuildForumDataComposer);
        {
            this.forum.serializeUserForum(this.response, this.habbo);

            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.forum.getGuild());
            GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, habbo);
            boolean isAdmin = member != null && (member.getRank() == GuildRank.MOD || member.getRank() == GuildRank.ADMIN || guild.getOwnerId() == this.habbo.getHabboInfo().getId());
            boolean isStaff = this.habbo.hasPermission("acc_modtool_ticket_q");

            String errorRead = "";
            String errorPost = "";
            String errorStartThread = "";
            String errorModerate = "";

            if (guild.canReadForum().state == 1 && member == null && !isStaff) {
                errorRead = "not_member";
            } else if (guild.canReadForum().state == 2 && !isAdmin && !isStaff) {
                errorRead = "not_admin";
            }

            if (guild.canPostMessages().state == 1 && member == null && !isStaff) {
                errorPost = "not_member";
            } else if (guild.canPostMessages().state == 2 && !isAdmin && !isStaff) {
                errorPost = "not_admin";
            } else if (guild.canPostMessages().state == 3 && guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorPost = "now_owner";
            }

            if (guild.canPostThreads().state == 1 && member == null && !isStaff) {
                errorStartThread = "not_member";
            } else if (guild.canPostThreads().state == 2 && !isAdmin && !isStaff) {
                errorStartThread = "not_admin";
            } else if (guild.canPostThreads().state == 3 && guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorStartThread = "now_owner";
            }

            if (guild.canModForum().state == 2 && !isAdmin && !isStaff) {
                errorModerate = "not_admin";
            } else if (guild.canModForum().state == 3 && guild.getOwnerId() != this.habbo.getHabboInfo().getId() && !isStaff) {
                errorModerate = "now_owner";
            }

            this.response.appendInt(guild.canReadForum().state);
            this.response.appendInt(guild.canPostMessages().state);
            this.response.appendInt(guild.canPostThreads().state);
            this.response.appendInt(guild.canModForum().state);
            this.response.appendString(errorRead);
            this.response.appendString(errorPost);
            this.response.appendString(errorStartThread);
            this.response.appendString(errorModerate);
            this.response.appendString(""); //citizen
            this.response.appendBoolean(guild.getOwnerId() == this.habbo.getHabboInfo().getId()); //Forum Settings
            this.response.appendBoolean(guild.getOwnerId() == this.habbo.getHabboInfo().getId() || isStaff); //Can Mod (staff)
        }
        return this.response;
    }
}