package com.eu.habbo.plugin.events.furniture;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class FurnitureRedeemedEvent extends FurnitureUserEvent
{
    public static final int CREDITS = 0;
    public static final int PIXELS = 1;
    public static final int DIAMONDS = 5;

    /**
     * The amount of the currency has been redeemed.
     */
    public final int amount;

    /**
     * The currencyID that was redeemed.
     */
    public final int currencyID;

    /**
     * This event is triggered whenever something happens to a furniture in a room.
     *
     * @param furniture  The furniture this event applies to.
     * @param habbo      The Habbo that redeemed this furniture.
     * @param amount     The amount of the currency has been redeemed.
     * @param currencyID The currencyID that was redeemed.
     */
    public FurnitureRedeemedEvent(HabboItem furniture, Habbo habbo, int amount, int currencyID)
    {
        super(furniture, habbo);

        this.amount = amount;
        this.currencyID = currencyID;
    }
}
