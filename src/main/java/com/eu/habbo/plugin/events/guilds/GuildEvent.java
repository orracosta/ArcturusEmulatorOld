package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.plugin.Event;

/**
 * Created on 24-10-2015 13:30.
 */
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
