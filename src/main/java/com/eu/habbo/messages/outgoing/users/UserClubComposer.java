package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserClubComposer extends MessageComposer
{
    private final Habbo habbo;

    public UserClubComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserClubComposer);

        this.response.appendString("club_habbo");

        int endTimestamp = this.habbo.getHabboStats().getClubExpireTimestamp();
        int now = Emulator.getIntUnixTimestamp();

        if(endTimestamp >= now)
        {
            /*Date endDate = new Date(endTimestamp * 1000);
            Date startDate = new Date(Emulator.getIntUnixTimestamp() * 1000);

            long duration = endDate.getTime() - startDate.getTime();

            long theDays = TimeUnit.MILLISECONDS.toDays(duration);
            int years = (int) Math.floor(theDays / 365);
            theDays = theDays - (years * 365);

            int months = (int) Math.floor(theDays / 31);
            theDays = theDays - (months * 31);*/

            int days = ((endTimestamp - Emulator.getIntUnixTimestamp()) / 86400);
            int years = (int) Math.floor(days / 365);

            //if(years > 0)
            //    days = days - (years * 365);

            int months = 0;

            if(days > 31)
            {
                months = (int) Math.floor(days / 31);
                days = days - (months * 31);
            }

            this.response.appendInt32(days);
            this.response.appendInt32(1);
            this.response.appendInt32(months);
            this.response.appendInt32(years);
        }
        else
        {
            this.response.appendInt32(0);
            this.response.appendInt32(7);
            this.response.appendInt32(0);
            this.response.appendInt32(1);
        }
        this.response.appendBoolean(true);
        this.response.appendBoolean(true);
        this.response.appendInt32(124);
        this.response.appendInt32(124);
        this.response.appendInt32(endTimestamp - Emulator.getIntUnixTimestamp());

        return this.response;
    }
}
