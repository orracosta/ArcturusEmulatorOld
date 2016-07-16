package com.eu.habbo.messages.outgoing.events.calendar;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class AdventCalendarProductComposer extends MessageComposer
{
    public final boolean visible;
    public final String productName;
    public final String customImage;
    public final String unknown;

    public AdventCalendarProductComposer(boolean visible, String productName, String customImage, String unknown)
    {
        this.visible = visible;
        this.productName = productName;
        this.customImage = customImage;
        this.unknown = unknown;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AdventCalendarProductComposer);
        this.response.appendBoolean(this.visible);
        this.response.appendString(this.productName);
        this.response.appendString(this.customImage);
        this.response.appendString(this.unknown);
        return this.response;
    }
}