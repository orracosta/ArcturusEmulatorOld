package com.eu.habbo.habbohotel.modtool;

/**
 * Created on 4-3-2015 17:10.
 */
public enum ModToolTicketState
{
    CLOSED(0),
    OPEN(1),
    PICKED(2);

    private final int state;

    ModToolTicketState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return this.state;
    }

    public static ModToolTicketState getState(int number)
    {
        for(ModToolTicketState s : ModToolTicketState.values())
        {
            if(s.state == number)
                return s;
        }

        return CLOSED;
    }
}
