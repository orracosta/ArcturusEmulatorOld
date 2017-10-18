package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.habbohotel.catalog.CalendarRewardObject;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AdventCalendarProductComposer extends MessageComposer
{
    public final boolean visible;
    public final CalendarRewardObject rewardObject;

    public AdventCalendarProductComposer(boolean visible, CalendarRewardObject rewardObject)
    {
        this.visible = visible;
        this.rewardObject = rewardObject;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AdventCalendarProductComposer);
        this.response.appendBoolean(this.visible);
        this.response.appendString(this.rewardObject.getName());
        this.response.appendString(this.rewardObject.getCustomImage());
        this.response.appendString(this.rewardObject.getCatalogItem() != null ? this.rewardObject.getCatalogItem().getName() : this.rewardObject.getName());
        return this.response;
    }
}