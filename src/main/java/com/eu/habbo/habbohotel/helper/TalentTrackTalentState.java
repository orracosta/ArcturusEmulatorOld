package com.eu.habbo.habbohotel.helper;

/**
 * Created on 29-12-2014 11:18.
 */
public enum TalentTrackTalentState
{
    CLOSED(0),
    PROGRESS(1),
    FINISHED(2);

    private final int state;

    TalentTrackTalentState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return this.state;
    }
}
