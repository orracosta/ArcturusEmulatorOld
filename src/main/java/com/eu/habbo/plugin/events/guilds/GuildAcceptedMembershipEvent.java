package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildAcceptedMembershipEvent extends GuildEvent
{
    /**
     * The userID that gets accepted.
     */
    public final int userId;

    /**
     * The Habbo that gets accepted. Can be NULL when user is offline.
     */
    public final Habbo user;

    /**
     * Triggered whenever an membership requests gets accepted.
     * This event cannot be cancelled.
     * @param guild The guild this applies to.
     * @param userId The userID that gets accepted.
     * @param user The Habbo that gets accepted. Can be NULL when user is offline.
     */
    public GuildAcceptedMembershipEvent(Guild guild, int userId, Habbo user)
    {
        super(guild);

        this.userId = userId;
        this.user = user;
    }
}
