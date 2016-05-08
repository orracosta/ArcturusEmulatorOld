package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 22-7-2015 17:15.
 */
public class HotelClosedAndOpensComposer extends MessageComposer
{
    private int hour;
    private int minute;

    public HotelClosedAndOpensComposer(int hour, int minute)
    {
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelClosedAndOpensComposer);
        this.response.appendInt32(this.hour);
        this.response.appendInt32(this.minute);
        return this.response;
    }
}
