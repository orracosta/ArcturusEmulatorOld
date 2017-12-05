package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;

public class BotWhisperEvent extends BotChatEvent
{
    /**
     * The target Habbo this message will be whispered to.
     */
    public Habbo target;

    /**
     * @param bot     The Bot this event applies to.
     * @param message The message the bot will say.
     * @param target  The target Habbo this message will be whispered to.
     */
    public BotWhisperEvent(Bot bot, String message, Habbo target)
    {
        super(bot, message);

        this.target = target;
    }
}
