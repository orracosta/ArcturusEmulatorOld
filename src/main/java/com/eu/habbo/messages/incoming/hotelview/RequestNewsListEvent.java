package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.*;

public class RequestNewsListEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new HotelViewDataComposer("2013-05-08 13:0", "gamesmaker"));
        this.client.sendResponse(new HallOfFameComposer());
        this.client.sendResponse(new NewsListComposer());
    }
}
