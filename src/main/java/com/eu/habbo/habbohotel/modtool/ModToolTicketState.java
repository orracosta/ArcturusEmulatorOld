package com.eu.habbo.habbohotel.modtool;

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
