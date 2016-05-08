package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        this.response.appendInt32(Emulator.getGameEnvironment().getCatalogManager().clubItems.size());

        for(CatalogItem item : Emulator.getGameEnvironment().getCatalogManager().clubItems)
        {
            this.response.appendInt32(item.getId());
            this.response.appendString(item.getName());
            this.response.appendBoolean(true); //unused
            this.response.appendInt32(item.getCredits());
            this.response.appendInt32(item.getPoints());
            this.response.appendInt32(item.getPointsType());
            this.response.appendBoolean(true);

            String[] data = item.getName().split("_");

            int days = 31;

            if(data[2].toLowerCase().equalsIgnoreCase("day"))
            {
                days = Integer.valueOf(data[3]);
            }
            else if(data[2].toLowerCase().equalsIgnoreCase("month"))
            {
                days = Integer.valueOf(data[3]) * 31;
            }
            else if(data[2].toLowerCase().equalsIgnoreCase("year"))
            {
                days = Integer.valueOf(data[3]) * 12 * 31;
            }

            this.response.appendInt32(days / 31); //months
            this.response.appendInt32(days);
            this.response.appendBoolean(true);
            this.response.appendInt32(days);

            int endTimestamp = this.habbo.getHabboStats().getClubExpireTimestamp();
            int now = Emulator.getIntUnixTimestamp();

            if(endTimestamp < Emulator.getIntUnixTimestamp())
                endTimestamp = now;

            Date endDate = new Date(endTimestamp * 1000);
            Date startDate = new Date(Emulator.getIntUnixTimestamp() * 1000);

            long duration  = endDate.getTime() - startDate.getTime();

            long theDays = TimeUnit.MILLISECONDS.toDays(duration);
            int years = (int)Math.floor(theDays / 365);
            theDays = theDays - (years * 365);

            int months = (int)Math.floor(theDays / 31);
            theDays = theDays - (months * 31);


            if(days >= 365)
            {
                years += (int)Math.floor(days / 365);
                days = days - (int)(Math.floor(days / 365) * 365);
            }

            if(days >= 31)
            {
                months += (int)Math.floor(days / 31);
                days = days - (int)(Math.floor(days / 31) * 31);
            }

            theDays += days;

            this.response.appendInt32((int)theDays);
            this.response.appendInt32(months);
            this.response.appendInt32(years);
        }

        this.response.appendInt32(this.windowId);
        return this.response;
    }
}
