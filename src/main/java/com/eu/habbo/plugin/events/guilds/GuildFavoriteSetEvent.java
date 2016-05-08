package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 24-10-2015 14:15.
 */
public class GuildFavoriteSetEvent extends GuildEvent
{
    /**
     * The Habbo that sets the favorite guild.
     */
    public final Habbo habbo;

    /**
     * Triggered whenever a Habbo sets his favorite guild.
     * @param guild The guild this applies to.
     * @param habbo The Habbo that sets the favorite guild.
     */
    public GuildFavoriteSetEvent(Guild guild, Habbo habbo)
    {
        super(guild);

        this.habbo = habbo;
    }
}
