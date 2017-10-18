package com.eu.habbo.habbohotel.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.set.hash.THashSet;

public class GameTeam
{
    /**
     * List of all players in this team.
     */
    private final THashSet<GamePlayer> members;

    /**
     * The team color of this team.
     */
    public final GameTeamColors teamColor;

    /**
     * The total score of this team.
     */
    private int teamScore;

    /**
     * Creates an new GameTeam with the given team color.
     * @param teamColor The team color this GameTeam is identified by.
     */
    public GameTeam(GameTeamColors teamColor)
    {
        this.teamColor = teamColor;

        this.members = new THashSet<GamePlayer>();
    }

    /**
     * Initialises the team and resets each player to it's default state.
     */
    public void initialise()
    {
        for(GamePlayer player : this.members)
        {
            player.reset();
        }

        this.teamScore = 0;
    }

    /**
     * Resets this team by clearing all members in the team.
     */
    public void reset()
    {
        this.members.clear();
    }

    /**
     * Adds an given amount of score to the team score.
     * @param teamScore The amount of score that will be added.
     */
    public void addTeamScore(int teamScore)
    {
        this.teamScore += teamScore;
    }

    /**
     * @return The team score of this team.
     */
    public int getTeamScore()
    {
        return this.teamScore;
    }

    /**
     * @return The sum of each team members score + the team score.
     */
    public synchronized int getTotalScore()
    {
        int score = this.teamScore;

        for(GamePlayer player : this.members)
        {
            score += player.getScore();
        }

        return score;
    }

    /**
     * Adds an GamePlayer to the team.
     * @param gamePlayer The GamePlayer that needs to be added.
     */
    public void addMember(GamePlayer gamePlayer)
    {
        synchronized (this.members)
        {
            this.members.add(gamePlayer);
        }
    }

    /**
     * Removes an GamePlayer from the team.
     * @param gamePlayer The GamePlayer that needs to be removed.
     */
    public void removeMember(GamePlayer gamePlayer)
    {
        synchronized (this.members)
        {
            this.members.remove(gamePlayer);
        }
    }

    /**
     * @return An collection of all GamePlayers.
     */
    public THashSet<GamePlayer> getMembers()
    {
        return this.members;
    }

    /**
     * Checks wether the given Habbo is part of this team.
     * @param habbo The habbo to check.
     * @return True when the given Habbo is part of the team.
     */
    public boolean isMember(Habbo habbo)
    {
        for(GamePlayer p : this.members)
        {
            if(p.getHabbo().equals(habbo))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the GamePlayer object for the given Habbo.
     * @param habbo The habbo to get the GamePlayer object for.
     * @return The GamePlayer for the given Habbo. If not present returns null.
     */
    @Deprecated
    public GamePlayer getPlayerForHabbo(Habbo habbo)
    {
        for(GamePlayer p : this.members)
        {
            if(p.getHabbo().equals(habbo))
            {
                return p;
            }
        }

        return null;
    }
}
