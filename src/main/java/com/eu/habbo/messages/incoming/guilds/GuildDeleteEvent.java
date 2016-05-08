package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.RemoveGuildFromRoomComposer;
import com.eu.habbo.plugin.events.guilds.GuildDeletedEvent;

/**
 * Created on 2-8-2015 15:09.
 */
public class GuildDeleteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = this.packet.readInt();

        if(this.client.getHabbo().getHabboStats().hasGuild(guildId))
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

            if(guild != null)
            {
                if (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId()) //TODO Add staff permission override.
                {
                    Emulator.getGameEnvironment().getGuildManager().deleteGuild(guild);
                    Emulator.getPluginManager().fireEvent(new GuildDeletedEvent(guild, this.client.getHabbo()));
                }

                Emulator.getGameEnvironment().getRoomManager().getRoom(guild.getRoomId()).sendComposer(new RemoveGuildFromRoomComposer(guildId).compose());
            }
        }
    }
}
