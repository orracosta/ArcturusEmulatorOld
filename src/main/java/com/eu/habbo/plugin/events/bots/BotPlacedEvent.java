package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 24-10-2015 13:34.
 */
public class BotPlacedEvent extends BotEvent
{
    /**
     * The location of the bot.
     */
    public final Tile location;

    /**
     * The placer of this bot.
     */
    public final Habbo placer;
    /**
     * @param bot The Bot this event applies to.
     * @param location The location of the bot.
     * @param placer The placer of this bot.
     */
    public BotPlacedEvent(Bot bot, Tile location, Habbo placer)
    {
        super(bot);

        this.location = location;
        this.placer = placer;
    }
}
