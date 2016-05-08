package com.eu.habbo.habbohotel.wired;

/**
 * Created on 22-10-2015 20:33.
 */
public enum WiredHighscoreClearType
{
    ALLTIME(0),
    DAILY(1),
    WEEKLY(2),
    MONTHLY(3);

    public final int type;

    WiredHighscoreClearType(int type)
    {
        this.type = type;
    }
}
