package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;

/**
 * Created on 28-8-2015 11:04.
 */
public class GameStartedEvent extends GameEvent
{
    /**
     * This event occurs when a Game starts.
     * @param game The Game this event applies to.
     */
    public GameStartedEvent(Game game)
    {
        super(game);
    }
}
