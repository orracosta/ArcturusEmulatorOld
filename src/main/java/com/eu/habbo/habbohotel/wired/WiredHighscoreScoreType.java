package com.eu.habbo.habbohotel.wired;

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
