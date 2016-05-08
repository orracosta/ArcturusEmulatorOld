package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.ClubGiftsComposer;

/**
 * Created on 25-7-2015 13:47.
 */
public class RequestClubGiftsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new ClubGiftsComposer());
    }
}
