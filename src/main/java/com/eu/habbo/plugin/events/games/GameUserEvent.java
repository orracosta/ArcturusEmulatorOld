package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.users.Habbo;

public abstract class GameUserEvent extends GameEvent
{
    /**
     * The Habbo that this event applies to.
     */
    public final Habbo habbo;

    /**
     * @param game The Game instance that this event applies to.
     * @param habbo The Habbo that this event applies to.
     */
    public GameUserEvent(Game game, Habbo habbo)
    {
        super(game);

        this.habbo = habbo;
    }
}
