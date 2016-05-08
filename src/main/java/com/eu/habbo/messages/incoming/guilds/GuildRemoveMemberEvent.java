package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildConfirmRemoveMemberComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildRefreshMembersListComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsComposer;
import com.eu.habbo.plugin.events.guilds.GuildRemovedMemberEvent;

public class GuildRemoveMemberEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();
        int userId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if (guild == null)
            return;

        if(guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() && userId != this.client.getHabbo().getHabboInfo().getId())
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
            GuildRemovedMemberEvent removedMemberEvent = new GuildRemovedMemberEvent(guild, userId, habbo);
            Emulator.getPluginManager().fireEvent(removedMemberEvent);
            if(removedMemberEvent.isCancelled())
                return;

            Emulator.getGameEnvironment().getGuildManager().removeMember(guild, userId);
            guild.decreaseMemberCount();
            this.client.sendResponse(new GuildRefreshMembersListComposer(guild));

            if (habbo != null)
            {
                habbo.getHabboStats().removeGuild(guild.getId());
                if (habbo.getHabboStats().guild == guildId)
                    habbo.getHabboStats().guild = 0;

                Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(guild.getRoomId());

                if (room != null)
                {
                    if (room.getGuildId() == guildId)
                    {
                        room.ejectUserFurni(userId);

                        habbo.getClient().sendResponse(new GuildInfoComposer(guild, habbo.getClient(), false, null));

                        room.refreshRightsForHabbo(habbo);
                    }
                }
            }
        }
    }
}
