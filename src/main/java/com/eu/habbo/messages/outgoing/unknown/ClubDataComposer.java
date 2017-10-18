package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Calendar;
import java.util.List;

public class ClubDataComposer extends MessageComposer
{
    private final int windowId;
    private final Habbo habbo;

    public ClubDataComposer(Habbo habbo, int windowId)
    {
        this.habbo = habbo;
        this.windowId = windowId;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ClubDataComposer);

        List<ClubOffer> offers = Emulator.getGameEnvironment().getCatalogManager().getClubOffers();
        this.response.appendInt(offers.size());

        //TODO Change this to a seperate table.
        for(ClubOffer offer : offers)
        {
            this.response.appendInt(offer.getId());
            this.response.appendString(offer.getName());
            this.response.appendBoolean(false); //unused
            this.response.appendInt(offer.getCredits());
            this.response.appendInt(offer.getPoints());
            this.response.appendInt(offer.getPointsType());
            this.response.appendBoolean(offer.isVip());

            long seconds = offer.getDays() * 86400l;

            long secondsTotal = seconds;

            int totalYears = (int)Math.floor((int)seconds / 86400 * 31 * 12);
            seconds -= totalYears * 86400 * 31 * 12;

            int totalMonths = (int)Math.floor((int)seconds / 86400 * 31);
            seconds -= totalMonths * 86400 * 31;

            int totalDays = (int)Math.floor((int)seconds / 86400);
            seconds -= totalDays * 86400;

            this.response.appendInt((int) secondsTotal / 86400 / 31);
            this.response.appendInt((int) seconds);
            this.response.appendBoolean(false); //giftable
            this.response.appendInt((int) seconds);

            int endTimestamp = habbo.getHabboStats().getClubExpireTimestamp();

            if (endTimestamp < Emulator.getIntUnixTimestamp())
            {
                endTimestamp = Emulator.getIntUnixTimestamp();
            }

            endTimestamp += secondsTotal;

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(endTimestamp * 1000l);
            this.response.appendInt(cal.get(Calendar.YEAR));
            this.response.appendInt(cal.get(Calendar.MONTH) + 1);
            this.response.appendInt(cal.get(Calendar.DAY_OF_MONTH));
        }

        this.response.appendInt(this.windowId);
        return this.response;
    }
}
