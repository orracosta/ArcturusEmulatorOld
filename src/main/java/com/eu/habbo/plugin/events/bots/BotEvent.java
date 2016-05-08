package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.plugin.Event;

/**
 * Created on 24-10-2015 13:09.
 */
public abstract class BotEvent extends Event
{
    /**
     * The Bot this event applies to.
     */
    public final Bot bot;

    /**
     * @param bot The Bot this event applies to.
     */
    public BotEvent(Bot bot)
    {
        this.bot = bot;
    }
}
