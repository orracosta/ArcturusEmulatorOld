package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;

public class BotSavedNameEvent extends BotEvent
{
    /**
     * Thew new name of the bot.
     */
    public String name;

    /**
     * This event is triggered whenever the name of a bot gets altered.
     * @param bot The Bot this event applies to.
     * @param name The new name of the bot.
     */
    public BotSavedNameEvent(Bot bot, String name)
    {
        super(bot);

        this.name = name;
    }
}
