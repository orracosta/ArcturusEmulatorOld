package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;

public abstract class BotChatEvent extends BotEvent
{
    /**
     * The message the bot will say.
     */
    public String message;

    /**
     * @param bot The Bot this event applies to.
     * @param message The message the bot will say.
     */
    public BotChatEvent(Bot bot, String message)
    {
        super(bot);

        this.message = message;
    }
}
