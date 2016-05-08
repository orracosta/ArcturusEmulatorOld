package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.GuildConfirmRemoveMemberComposer;

/**
 * Created on 27-7-2015 18:58.
 */
public class GuildConfirmRemoveMemberEvent extends MessageHandler
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
            Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(guild.getRoomId());

            if(room != null)
            {
                this.client.sendResponse(new GuildConfirmRemoveMemberComposer(userId, room.getUserFurniCount(userId)));
            }
        }
    }
}
