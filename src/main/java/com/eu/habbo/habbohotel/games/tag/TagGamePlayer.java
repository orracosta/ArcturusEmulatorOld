package com.eu.habbo.habbohotel.games.tag;

import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.users.Habbo;

public class TagGamePlayer extends GamePlayer
{
    /**
     * Creates a new GamePlayer for the given Habbo and it's team.
     *
     * @param habbo     The Habbo who is linked to this GamePlayer.
     * @param teamColor The team the GamePlayer is in.
     */
    public TagGamePlayer(Habbo habbo, GameTeamColors teamColor)
    {
        super(habbo, teamColor);
    }
}