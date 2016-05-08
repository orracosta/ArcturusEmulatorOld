package com.eu.habbo.habbohotel.wired;

/**
 * Created on 13-12-2014 22:49.
 */
public enum WiredTriggerType
{
    SAY_SOMETHING(0),
    WALKS_ON_FURNI(1),
    WALKS_OFF_FURNI(2),
    AT_GIVEN_TIME(3),
    STATE_CHANGED(4),
    PERIODICALLY(6),
    ENTER_ROOM(7),
    GAME_STARTS(8),
    GAME_ENDS(9),
    SCORE_ACHIEVED(10),
    COLLISION(11),
    PERIODICALLY_LONG(12),
    BOT_REACHED_STF(13),
    BOT_REACHED_AVTR(14);

    public final int code;

    WiredTriggerType(int code)
    {
        this.code = code;
    }
}
