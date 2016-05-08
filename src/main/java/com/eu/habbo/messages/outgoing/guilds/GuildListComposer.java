package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 24-11-2014 19:33.
 */
public class GuildListComposer extends MessageComposer
{
    private final THashSet<Guild> guilds;
    private final Habbo habbo;

    public GuildListComposer(THashSet<Guild> guilds, Habbo habbo)
    {
        this.guilds = guilds;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildListComposer);
        this.response.appendInt32(this.guilds.size());
        for(Guild guild : this.guilds)
        {
            this.response.appendInt32(guild.getId());
            this.response.appendString(guild.getName());
            this.response.appendString(guild.getBadge());
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorOne()).valueA);
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getBackgroundColor(guild.getColorTwo()).valueA);
            this.response.appendBoolean(this.habbo.getHabboStats().guild == guild.getId());
            this.response.appendInt32(0);
            this.response.appendBoolean(false);
        }
        return this.response;
    }
}
