package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;

public class BotTalkEvent extends BotChatEvent
{
    /**
     * @param bot     The Bot this event applies to.
     * @param message The message the bot will say.
     */
    public BotTalkEvent(Bot bot, String message)
    {
        super(bot, message);
    }
}
