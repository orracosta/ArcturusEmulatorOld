package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.*;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.messages.outgoing.users.UserDataComposer;
import com.eu.habbo.messages.outgoing.users.UserHomeRoomComposer;

/**
 * Created on 27-8-2014 11:25.
 */
public class HotelViewRequestBonusRareEvent extends MessageHandler
{

    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new BonusRareComposer(this.client.getHabbo()));
    }
}
