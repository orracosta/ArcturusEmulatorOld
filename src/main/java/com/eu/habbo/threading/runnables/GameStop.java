package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.games.Game;

public class GameStop implements Runnable
{
    private Game game;

    public GameStop(Game game)
    {
        this.game = game;
    }

    @Override
    public void run()
    {
        this.game.stop();
    }
}
