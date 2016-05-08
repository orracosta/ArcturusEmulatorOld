package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;

public class BotPickedUpEvent extends BotEvent
{
    /**
     * The Habbo who picked the bot up.
     */
    public final Habbo picker;

    /**
     * @param bot The Bot this event applies to.
     */
    public BotPickedUpEvent(Bot bot, Habbo picker)
    {
        super(bot);

        this.picker = picker;
    }
}
