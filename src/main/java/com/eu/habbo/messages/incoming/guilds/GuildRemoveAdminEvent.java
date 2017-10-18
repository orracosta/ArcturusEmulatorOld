package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildConfirmRemoveMemberComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildMemberUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsComposer;
import com.eu.habbo.plugin.events.guilds.GuildRemovedAdminEvent;

public class GuildRemoveAdminEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if(guild != null)
        {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_guild_admin"))
            {
                int userId = this.packet.readInt();

                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId());
                Emulator.getGameEnvironment().getGuildManager().removeAdmin(guild, userId);

                Habbo habbo = room.getHabbo(userId);
                GuildRemovedAdminEvent removedAdminEvent = new GuildRemovedAdminEvent(guild, userId, habbo);
                Emulator.getPluginManager().fireEvent(removedAdminEvent);

                if (removedAdminEvent.isCancelled())
                    return;

                if (habbo != null)
                {
                    habbo.getClient().sendResponse(new GuildInfoComposer(guild, this.client, false, Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild.getId(), userId)));

                    room.refreshRightsForHabbo(habbo);
                }
                GuildMember guildMember = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, userId);

                this.client.sendResponse(new GuildMemberUpdateComposer(guild, guildMember));
            }
        }
    }
}
