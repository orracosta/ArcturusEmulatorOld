package com.eu.habbo.plugin.events.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

public class BotPlacedEvent extends BotEvent
{
    /**
     * The location of the bot.
     */
    public final RoomTile location;

    /**
     * The placer of this bot.
     */
    public final Habbo placer;
    /**
     * @param bot The Bot this event applies to.
     * @param location The location of the bot.
     * @param placer The placer of this bot.
     */
    public BotPlacedEvent(Bot bot, RoomTile location, Habbo placer)
    {
        super(bot);

        this.location = location;
        this.placer = placer;
    }
}
