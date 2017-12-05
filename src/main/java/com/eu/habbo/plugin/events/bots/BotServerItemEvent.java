package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;

public class BotServerItemEvent extends BotEvent
{
    /**
     * The Habbo that will be served.
     */
    public Habbo habbo;
    /**
     * The handitem that will be served.
     */
    public int itemId;

    /**
     * This event is triggered when a bot serves a handitem to a Habbo.
     * The itemId and Habbo can be changed.
     * @param bot The Bot this event applies to.
     * @param itemId The habditem that will be served.
     * @param habbo The Habbo that will be served.
     */
    public BotServerItemEvent(Bot bot, Habbo habbo, int itemId)
    {
        super(bot);

        this.habbo = habbo;
        this.itemId = itemId;
    }
}