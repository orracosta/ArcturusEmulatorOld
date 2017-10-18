package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;

public class GuildChangedColorsEvent extends GuildEvent
{
    /**
     * First color.
     */
    public int colorOne;

    /**
     * Second color.
     */
    public int colorTwo;

    /**
     * Triggered whenever the guild colors get updated.
     * @param guild The guild this applies to.
     * @param colorOne First color.
     * @param colorTwo Second color.
     */
    public GuildChangedColorsEvent(Guild guild, int colorOne, int colorTwo)
    {
        super(guild);

        this.colorOne = colorOne;
        this.colorTwo = colorTwo;
    }
}
