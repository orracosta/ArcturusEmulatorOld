package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

import java.util.ArrayList;
import java.util.List;

public class AdventCalendarDataComposer extends MessageComposer
{
    public final String eventName;
    public final int totalDays;
    public final int currentDay;
    public final TIntArrayList unlocked;
    public final boolean lockExpired;

    public AdventCalendarDataComposer(String eventName, int totalDays, int currentDay, TIntArrayList unlocked, boolean lockExpired)
    {
        this.eventName   = eventName;
        this.totalDays   = totalDays;
        this.currentDay  = currentDay;
        this.unlocked    = unlocked;
        this.lockExpired = lockExpired;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AdventCalendarDataComposer);
        this.response.appendString(this.eventName);
        this.response.appendString("");
        this.response.appendInt(this.currentDay);
        this.response.appendInt(this.totalDays);

        this.response.appendInt(this.unlocked.size());

        TIntArrayList expired = new TIntArrayList();
        for (int i = 0; i < totalDays; i++)
        {
            expired.add(i);
            expired.remove(this.currentDay);
        }

        this.unlocked.forEach(new TIntProcedure()
        {
            @Override
            public boolean execute(int value)
            {
                response.appendInt(value);
                expired.remove(value);
                return true;
            }
        });


        if (this.lockExpired)
        {
            this.response.appendInt(expired.size());
            expired.forEach(new TIntProcedure()
            {
                @Override
                public boolean execute(int value)
                {
                    response.appendInt(value);
                    return true;
                }
            });
        }
        else
        {
            this.response.appendInt(0);
        }

        return this.response;
    }
}