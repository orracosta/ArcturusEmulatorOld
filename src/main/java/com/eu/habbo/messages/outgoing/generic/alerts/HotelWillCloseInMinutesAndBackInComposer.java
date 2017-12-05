package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HotelWillCloseInMinutesAndBackInComposer extends MessageComposer
{
    private int closeInMinutes;
    private int reopenInMinutes;

    public HotelWillCloseInMinutesAndBackInComposer(int closeInMinutes, int reopenInMinutes)
    {
        this.closeInMinutes = closeInMinutes;
        this.reopenInMinutes = reopenInMinutes;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelWillCloseInMinutesAndBackInComposer);
        this.response.appendBoolean(true);
        this.response.appendInt(this.closeInMinutes);
        this.response.appendInt(this.reopenInMinutes);
        return this.response;
    }
}
