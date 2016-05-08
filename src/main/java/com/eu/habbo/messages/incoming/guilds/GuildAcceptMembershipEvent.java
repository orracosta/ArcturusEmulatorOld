package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildRefreshMembersListComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsComposer;
import com.eu.habbo.plugin.events.guilds.GuildAcceptedMembershipEvent;

/**
 * Created on 23-11-2014 15:42.
 */
public class GuildAcceptMembershipEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId() && !this.client.getHabbo().hasPermission("acc_guild_admin"))
            return;

        Emulator.getGameEnvironment().getGuildManager().joinGuild(guild, this.client, userId, true);
        guild.decreaseRequestCount();
        this.client.sendResponse(new GuildRefreshMembersListComposer(guild));

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        Emulator.getPluginManager().fireEvent(new GuildAcceptedMembershipEvent(guild, userId, habbo));

        if(habbo != null)
        {
            habbo.getHabboStats().addGuild(guild.getId());
            Room room = habbo.getHabboInfo().getCurrentRoom();
            if(room != null)
            {
                if(room.getGuildId() == guildId)
                {
                    habbo.getClient().sendResponse(new GuildInfoComposer(guild, habbo.getClient(), false, Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, userId)));

                    room.refreshRightsForHabbo(habbo);
                }
            }
        }
    }
}
