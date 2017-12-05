package com.eu.habbo.plugin.events.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;

public class GuildRemovedMemberEvent extends GuildEvent
{
    /**
     * The userID of the Habbo being removed.
     */
    public final int userId;

    /**
     * The guildmember that has been removed.
     */
    public final Habbo guildMember;

    /**
     * @param guild The guild this applies to.
     * @param userId The userID of the Habbo being removed.
     * @param guildMember The guildmember that has been removed.
     */
    public GuildRemovedMemberEvent(Guild guild, int userId, Habbo guildMember)
    {
        super(guild);
        this.guildMember = guildMember;
        this.userId = userId;
    }
}
