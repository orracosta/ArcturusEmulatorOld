package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.users.Habbo;

public class SupportUserBannedEvent extends SupportEvent
{
    /**
     * The Habbo that has been banned.
     */
    public final Habbo target;

    /**
     * The ModToolBan created.
     */
    public final ModToolBan ban;

    /**
     * This event is triggered when a Habbo gets banned. Changes to the ModToolBan will be saved.
     * @param target The Habbo that has been banned.
     * @param ban The ModToolBan created.
     */
    public SupportUserBannedEvent(Habbo moderator, Habbo target, ModToolBan ban)
    {
        super(moderator);

        this.target = target;
        this.ban    = ban;
    }
}