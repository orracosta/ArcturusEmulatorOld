package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 16-11-2015 21:38.
 */
public class HotelViewClaimBadgeEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String request = this.packet.readString();
    }
}
