package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildGivenAdminEvent extends GuildEvent
{
    /**
     * The userId of the Habbo given Admin rights.
     */
    public final int userId;

    /**
     * The Habbo that is given Admin rights.
     */
    public final Habbo habbo;

    /**
     * The person that give the Habbo admin rights.
     */
    public final Habbo admin;

    /**
     * Triggered whenever an Habbo gets given the Admin rights.
     * @param guild The guild this applies to.
     * @param userId The userId of the Habbo given Admin rights.
     * @param habbo The Habbo that is given Admin rights.
     * @param admin The person that give the Habbo admin rights.
     */
    public GuildGivenAdminEvent(Guild guild, int userId, Habbo habbo, Habbo admin)
    {
        super(guild);
        this.userId = userId;
        this.habbo = habbo;
        this.admin = admin;
    }
}
