package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;

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
