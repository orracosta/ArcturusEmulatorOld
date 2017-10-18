package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildRemovedAdminEvent extends GuildEvent
{
    /**
     * The userID of the Admin that gets removed.
     */
    public final int userId;

    /**
     * The Habbo that gets removed. Is null when the Habbo is offnot in the guild room.
     */
    public final Habbo admin;

    /**
     * @param guild The guild this applies to.
     * @param userId The userID of the Admin that gets removed.
     * @param admin The Habbo that gets removed. Is null when the Habbo is not in the guild room.
     */
    public GuildRemovedAdminEvent(Guild guild, int userId, Habbo admin)
    {
        super(guild);
        this.userId = userId;
        this.admin = admin;
    }
}
