package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUsersAddGuildBadgeComposer extends MessageComposer
{
    public final Guild guild;

    public RoomUsersAddGuildBadgeComposer(Guild guild)
    {
        this.guild = guild;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUsersGuildBadgesComposer);
        this.response.appendInt(1);
        this.response.appendInt(this.guild.getId());
        this.response.appendString(this.guild.getBadge());
        return this.response;
    }
}