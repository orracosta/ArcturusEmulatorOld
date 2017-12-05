package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildPurchasedEvent extends GuildEvent
{
    /**
     * The Habbo that bought this guild.
     */
    public final Habbo habbo;

    /**
     * Triggered whenever someone buys a guild.
     * @param guild The guild this applies to.
     * @param habbo
     */
    public GuildPurchasedEvent(Guild guild, Habbo habbo)
    {
        super(guild);
        this.habbo = habbo;
    }
}
