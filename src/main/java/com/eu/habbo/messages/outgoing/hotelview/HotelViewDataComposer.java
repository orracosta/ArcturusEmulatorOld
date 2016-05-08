package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-8-2014 12:56.
 */
public class HotelViewDataComposer extends MessageComposer {

    private final String data;
    private final String key;

    public HotelViewDataComposer(String data, String key)
    {
        this.data = data;
        this.key = key;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.HotelViewDataComposer);

        this.response.appendString(this.data);
        this.response.appendString(this.key);

        return this.response;
    }
}
