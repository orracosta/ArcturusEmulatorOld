package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AdventCalendarDataComposer extends MessageComposer
{
    public final String eventName;
    public final int totalDays;
    public final int currentDay;
    public final int[] unlocked;
    public final int[] expired;

    public AdventCalendarDataComposer(String eventName, int totalDays, int currentDay, int[] unlocked, int[] expired)
    {
        this.eventName = eventName;
        this.totalDays = totalDays;
        this.currentDay = currentDay;
        this.unlocked = unlocked;
        this.expired = expired;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AdventCalendarDataComposer);
        this.response.appendString(this.eventName);
        this.response.appendString(this.eventName);
        this.response.appendInt32(this.currentDay);
        this.response.appendInt32(this.totalDays);

        this.response.appendInt32(this.unlocked.length);
        for (int unlocked : this.unlocked)
        {
            this.response.appendInt32(unlocked);
        }

        this.response.appendInt32(this.expired.length);
        for (int expired : this.expired)
        {
            this.response.appendInt32(expired);
        }

        return this.response;
    }
}