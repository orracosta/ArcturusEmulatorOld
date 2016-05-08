package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 24-10-2015 14:05.
 */
public class GuildRemovedFavoriteEvent extends GuildEvent
{
    /**
     * The Habbo that removed the guild as favorite.
     */
    public final Habbo habbo;

    /**
     * @param guild The guild this applies to.
     * @param habbo The Habbo that removed the guild as favorite.
     */
    public GuildRemovedFavoriteEvent(Guild guild, Habbo habbo)
    {
        super(guild);
        this.habbo = habbo;
    }
}
