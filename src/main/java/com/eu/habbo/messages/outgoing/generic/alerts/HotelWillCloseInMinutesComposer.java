package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 15:25.
 */
public class HotelWillCloseInMinutesComposer extends MessageComposer
{
    private int minutes;

    public HotelWillCloseInMinutesComposer(int minutes)
    {
        this.minutes = minutes;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelWillCloseInMinutesComposer);
        this.response.appendInt32(this.minutes);
        return this.response;
    }
}
