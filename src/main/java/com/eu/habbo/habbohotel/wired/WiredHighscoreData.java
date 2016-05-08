package com.eu.habbo.habbohotel.wired;

import gnu.trove.set.hash.THashSet;

/**
 * Created on 22-10-2015 20:37.
 */
public class WiredHighscoreData
{
    public String[] usernames;
    public int score;
    public int teamScore;
    public int totalScore;

    public WiredHighscoreData(String[] usernames, int score, int teamScore, int totalScore)
    {
        this.usernames = usernames;

        this.score = score;
        this.teamScore = teamScore;
        this.totalScore = totalScore;
    }
}
