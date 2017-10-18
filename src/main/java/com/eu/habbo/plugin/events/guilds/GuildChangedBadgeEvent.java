package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;

public class GuildChangedBadgeEvent extends GuildEvent
{
    /**
     * The new badge of the guild.
     */
    public String badge;

    /**
     * Triggered whenever the badge of a guild gets updated.
     * @param guild The guild this applies to.
     * @param badge The new badge of the guild.
     */
    public GuildChangedBadgeEvent(Guild guild, String badge)
    {
        super(guild);

        this.badge = badge;
    }
}
