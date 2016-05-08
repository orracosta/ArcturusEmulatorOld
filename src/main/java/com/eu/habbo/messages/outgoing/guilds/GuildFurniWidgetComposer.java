package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildFurniWidgetComposer extends MessageComposer
{
    private final HabboItem item;
    private final Guild guild;
    private final Habbo habbo;

    public GuildFurniWidgetComposer(Habbo habbo, Guild guild, HabboItem item)
    {
        this.habbo = habbo;
        this.item = item;
        this.guild = guild;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildFurniWidgetComposer);
        this.response.appendInt32(item.getId());
        this.response.appendInt32(this.guild.getId());
        this.response.appendString(this.guild.getName());
        this.response.appendInt32(this.guild.getRoomId());
        this.response.appendBoolean(Emulator.getGameEnvironment().getGuildManager().getGuildMember(this.guild, this.habbo) != null); //User Joined.
        this.response.appendBoolean(false); //Has Forum.
        return this.response;
    }
}
