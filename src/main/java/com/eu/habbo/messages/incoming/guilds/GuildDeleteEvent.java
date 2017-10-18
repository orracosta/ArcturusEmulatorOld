package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.RemoveGuildFromRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomDataComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomEntryInfoComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomSettingsComposer;
import com.eu.habbo.plugin.events.guilds.GuildDeletedEvent;

public class GuildDeleteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if(guild != null)
        {
            if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_guild_admin")) //TODO Add staff permission override.
            {
                Emulator.getGameEnvironment().getGuildManager().deleteGuild(guild);
                Emulator.getPluginManager().fireEvent(new GuildDeletedEvent(guild, this.client.getHabbo()));
                Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId()).sendComposer(new RemoveGuildFromRoomComposer(guildId).compose());

                if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
                {
                    if (guild.getRoomId() == this.client.getHabbo().getHabboInfo().getCurrentRoom().getId())
                    {
                        this.client.sendResponse(new RoomDataComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom(), this.client.getHabbo(), false, false));
                    }
                }
            }
        }
    }
}
