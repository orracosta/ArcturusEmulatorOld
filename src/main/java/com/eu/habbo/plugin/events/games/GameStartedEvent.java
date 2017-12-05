package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;

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
