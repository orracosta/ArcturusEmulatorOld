package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 24-8-2014 17:25.
 */
public class HotelViewComposer extends MessageComposer {

    @Override
    public ServerMessage compose() { //2553
        //1945 ??
        this.response.init(Outgoing.HotelViewComposer);
        return this.response;
    }
}
