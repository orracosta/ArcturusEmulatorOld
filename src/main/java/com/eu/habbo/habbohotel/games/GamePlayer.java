package com.eu.habbo.habbohotel.games;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

public class GamePlayer
{
    /**
     * The Habbo that is linked to the game player.
     */
    private Habbo habbo;

    /**
     * The team color of this player.
     */
    private GameTeamColors teamColor;

    /**
     * The score of this player.
     *
     * This score gets saved at the end of a game for the Wired HighScore furniture.
     */
    private int score;

    /**
     * Creates a new GamePlayer for the given Habbo and it's team.
     * @param habbo The Habbo who is linked to this GamePlayer.
     * @param teamColor The team the GamePlayer is in.
     */
    public GamePlayer(Habbo habbo, GameTeamColors teamColor)
    {
        this.habbo = habbo;
        this.teamColor = teamColor;
    }

    /**
     * Resets the GamePlayer object to it's 'default' state.
     * This method will be called by the emulator:
     * <ul>
     *     <li>The game starts.</li>
     *     <li>The game ends.</li>
     * </ul>
     */
    public void reset()
    {
        this.score = 0;
    }

    /**
     * Adds an given amount of score to this object.
     * @param amount The amount of score to add.
     */
    public synchronized void addScore(int amount)
    {
        this.score += amount;
        WiredHandler.handle(WiredTriggerType.SCORE_ACHIEVED, null, this.habbo.getHabboInfo().getCurrentRoom(), new Object[]{this.habbo.getHabboInfo().getCurrentRoom().getGame(this.habbo.getHabboInfo().getCurrentGame()).getTeamForHabbo(this.habbo).getTotalScore(), amount});
    }

    /**
     * @return The Habbo linked to this object.
     */
    public Habbo getHabbo()
    {
        return this.habbo;
    }

    /**
     * @return The GameTeamColor for this object.
     */
    public GameTeamColors getTeamColor()
    {
        return this.teamColor;
    }

    /**
     * @return The score amount for this object.
     */
    public int getScore()
    {
        return this.score;
    }
}
