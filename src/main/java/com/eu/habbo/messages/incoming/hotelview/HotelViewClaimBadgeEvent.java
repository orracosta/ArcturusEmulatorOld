package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;

public class HotelViewClaimBadgeEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String request = this.packet.readString();
    }
}
