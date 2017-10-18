package com.eu.habbo.habbohotel.catalog.marketplace;

public enum MarketPlaceState
{
    /**
     * When the offer is available to be bought.
     */
    OPEN(1),

    /**
     * When the offer has been sold.
     */
    SOLD(2),

    /**
     * When the offer has been expired.
     * When the offer has been cancelled.
     */
    CLOSED(3);

    private final int state;

    MarketPlaceState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return this.state;
    }

    /**
     * Gets the offer state for the type.
     * @param type The type to look for.
     * @return The offer state defaults to CLOSED
     */
    public static MarketPlaceState getType(int type)
    {
        switch(type)
        {
            case 1: return OPEN;
            case 2: return SOLD;
            case 3: return CLOSED;
        }

        return CLOSED;
    }

}
