package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

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
        this.response.appendInt(this.minutes);
        return this.response;
    }
}
