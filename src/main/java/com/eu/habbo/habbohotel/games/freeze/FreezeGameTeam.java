package com.eu.habbo.habbohotel.games.freeze;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;

public class FreezeGameTeam extends GameTeam
{
    public FreezeGameTeam(GameTeamColors teamColor)
    {
        super(teamColor);
    }

    @Override
    public void removeMember(GamePlayer gamePlayer)
    {
        Game game = gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().getGame(FreezeGame.class);
        if(game != null && game instanceof FreezeGame)
        {
            ((FreezeGame) game).placebackHelmet(gamePlayer.getTeamColor());
        }

        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), 0);
        gamePlayer.getHabbo().getRoomUnit().setCanWalk(true);

        super.removeMember(gamePlayer);
    }

    @Override
    public void addMember(GamePlayer gamePlayer)
    {
        super.addMember(gamePlayer);

        gamePlayer.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gamePlayer.getHabbo(), FreezeGame.effectId + this.teamColor.type);
    }
}
