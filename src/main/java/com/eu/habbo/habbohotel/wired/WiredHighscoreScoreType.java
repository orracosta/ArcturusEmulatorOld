package com.eu.habbo.habbohotel.wired;

/**
 * Created on 22-10-2015 20:28.
 */
public enum WiredHighscoreScoreType
{
    PERTEAM(0),
    MOSTWIN(1),
    CLASSIC(2);

    public final int type;

    WiredHighscoreScoreType(int type)
    {
        this.type = type;
    }
}
