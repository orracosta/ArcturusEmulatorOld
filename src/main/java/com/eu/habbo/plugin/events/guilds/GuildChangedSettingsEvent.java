package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;

public class GuildChangedSettingsEvent extends GuildEvent
{
    /**
     * The new state of the guild.
     */
    public int state;

    /**
     * The new rights of the guild.
     */
    public int rights;
    /**
     * Triggered whenever the settings of a guild gets updated.
     * @param guild The guild this applies to.
     * @param state The new state of the guild.
     * @param rights The new rights of the guild.
     */
    public GuildChangedSettingsEvent(Guild guild, int state, int rights)
    {
        super(guild);
        this.state = state;
        this.rights = rights;
    }
}
