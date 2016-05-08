package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 15:26.
 */
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
        this.response.appendInt32(this.closeInMinutes);
        this.response.appendInt32(this.reopenInMinutes);
        return this.response;
    }
}
