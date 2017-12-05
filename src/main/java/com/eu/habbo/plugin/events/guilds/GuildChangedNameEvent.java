package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;

public class GuildChangedNameEvent extends GuildEvent
{
    /**
     * The new name of the guild.
     */
    public String name;

    /**
     * The new description of the guild.
     */
    public String description;

    /**
     * Triggered whenever the name / description of a guild gets updated.
     * @param guild The guild this applies to.
     * @param name The new name of the guild.
     * @param description The new description of the guild.
     */
    public GuildChangedNameEvent(Guild guild, String name, String description)
    {
        super(guild);
        this.name = name;
        this.description = description;
    }
}
