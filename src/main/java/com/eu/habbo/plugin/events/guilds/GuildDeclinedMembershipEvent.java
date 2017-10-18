package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildDeclinedMembershipEvent extends GuildEvent
{
    /**
     * The userID that gets declined.
     */
    public final int userId;

    /**
     * The Habbo that gets declined. Can be NULL when user is offline.
     */
    public final Habbo user;

    /**
     * The Habbo that declined the user from joining.
     */
    public final Habbo admin;

    /**
     * Triggered whenever an membership requests gets declined.
     * This event cannot be cancelled.
     * @param guild The guild this applies to.
     * @param userId The userID that gets declined.
     * @param user The Habbo that gets declined. Can be NULL when user is offline.
     */
    public GuildDeclinedMembershipEvent(Guild guild, int userId, Habbo user, Habbo admin)
    {
        super(guild);

        this.userId = userId;
        this.user = user;
        this.admin = admin;
    }
}
