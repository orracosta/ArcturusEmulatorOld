package com.eu.habbo.messages.outgoing.generic;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PickMonthlyClubGiftNotificationComposer extends MessageComposer
{
    private int count;

    public PickMonthlyClubGiftNotificationComposer(int count)
    {
        this.count = count;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PickMonthlyClubGiftNotificationComposer);
        this.response.appendInt(this.count);
        return this.response;
    }
}
