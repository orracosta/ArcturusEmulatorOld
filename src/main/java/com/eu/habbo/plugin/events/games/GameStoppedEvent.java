package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;

/**
 * Created on 28-8-2015 11:06.
 */
public class GameStoppedEvent extends GameEvent
{
    /**
     * This event occurs when a Game stops.
     * @param game The Game instance that this event applies to.
     */
    public GameStoppedEvent(Game game)
    {
        super(game);
    }
}
