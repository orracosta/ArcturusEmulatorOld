package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.plugin.Event;

public abstract class GuildEvent extends Event
{
    /**
     * The guild this applies to.
     */
    public final Guild guild;

    /**
     * @param guild The guild this applies to.
     */
    public GuildEvent(Guild guild)
    {
        this.guild = guild;
    }
}
