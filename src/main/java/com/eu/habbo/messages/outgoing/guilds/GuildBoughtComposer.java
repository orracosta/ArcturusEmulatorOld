package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 22-11-2014 20:23.
 */
public class GuildBoughtComposer extends MessageComposer
{
    private final Guild guild;

    public GuildBoughtComposer(Guild guild)
    {
        this.guild = guild;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildBoughtComposer);
        this.response.appendInt32(this.guild.getRoomId());
        this.response.appendInt32(this.guild.getId());
        return this.response;
    }
}
