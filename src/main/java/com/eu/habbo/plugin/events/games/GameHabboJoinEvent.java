package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.users.Habbo;

public class GameHabboJoinEvent extends GameUserEvent
{
    /**
     * @param game  The Game instance that this event applies to.
     * @param habbo The Habbo that joined the game.
     */
    public GameHabboJoinEvent(Game game, Habbo habbo)
    {
        super(game, habbo);
    }
}
