package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildDeletedEvent extends GuildEvent
{
    /**
     * The Habbo that deleted the guild.
     */
    public final Habbo deleter;

    /**
     * Triggered whenever a guild gets deleted.
     * @param guild The guild this applies to.
     * @param deleter The Habbo that deleted the guild.
     */
    public GuildDeletedEvent(Guild guild, Habbo deleter)
    {
        super(guild);

        this.deleter = deleter;
    }
}
